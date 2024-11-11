package com.AuthService.Controller;

import com.AuthService.Entity.Role;
import com.AuthService.Entity.User;
import com.AuthService.config.config;
import com.AuthService.security.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
/**
 * The {@code AuthenticationFilterUpdate} class is a custom Spring Cloud Gateway filter
 * responsible for managing authentication and authorization of incoming requests.
 *
 * This filter checks whether requests require authentication, extracts and validates
 * JWT tokens, interacts with a user service to validate user details, and caches
 * authenticated user information in Redis for improved performance.
 */

@Component
public class AuthenticationFilterUpdate extends AbstractGatewayFilterFactory<AuthenticationFilterUpdate.Config> {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationFilterUpdate.class);

    @Autowired
    private RouteValidator validator;

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RedisTemplate<String, UserDetails> redisTemplate;

    /**
     * Constructs an instance of {@code AuthenticationFilterUpdate}.
     */

    public AuthenticationFilterUpdate() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            // Check if the request is secured
            if (validator.isSecured.test(request)) {
                if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                    logger.warn("Missing authorization header");
                    return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing authorization header"));
                }

                // Extract token
                String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
                String token = jwtUtil.extractToken(authHeader);

                // Check if the token is expired or about to expire (within 5 minutes)
                if (jwtUtil.isTokenExpired(token) || jwtUtil.isTokenAboutToExpire(token, 5 * 60 * 1000)) {
                    logger.warn("Token has expired");

                    // Check for a valid refresh token in the request
                    String refreshToken = request.getHeaders().getFirst("Refresh-Token");
                    if (refreshToken != null) {
                        return refreshAccessToken(refreshToken, chain, exchange);
                    } else {
                        return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token has expired and no refresh token provided"));
                    }
                }

                // Check Redis cache for token validation
                ValueOperations<String, UserDetails> ops = redisTemplate.opsForValue();
                UserDetails cachedUserDetails = ops.get(token);

                if (cachedUserDetails != null) {
                    logger.info("Using cached user details for token: {}", token);

                    // Use cached user details
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                            cachedUserDetails, null, cachedUserDetails.getAuthorities()
                    );

                    return chain.filter(exchange)
                            .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authenticationToken));
                }

                // Validate token and extract username
                String username = validateAndGetUsername(token);
                if (username == null) {
                    // If username is null, return an unauthorized response
                    return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized access: Invalid token"));
                }
                // Call user service to validate user
                return validateUserFromService(chain, exchange, ops, token, username);
            }

            // Proceed with the request if not secured
            return chain.filter(exchange);
        };
    }

    /**
     * Validates the JWT token and retrieves the associated username.
     *
     * @param token the JWT token to validate
     * @return the username extracted from the token
     * @throws ResponseStatusException if the token is invalid or unauthorized
     */

    private String validateAndGetUsername(String token) {
        try {
            jwtUtil.validateToken(token);
            return jwtUtil.getUsernameFromJWT(token);
        } catch (ExpiredJwtException e) {
            logger.warn("Token has expired: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token has expired");
        } catch (JwtException e) {
            logger.error("Invalid token: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
        } catch (Exception e) {
            logger.error("Unauthorized access attempt: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized access: " + e.getMessage());
        }
    }

    /**
     * Validates the user by calling the user service and caching the user details in Redis.
     *
     * @param chain the filter chain
     * @param exchange the current server exchange
     * @param ops the Redis value operations for caching user details
     * @param token the JWT token
     * @param username the username to validate
     * @return a Mono<Void> representing the completion of the request processing
     */

    private Mono<Void> validateUserFromService(GatewayFilterChain chain, ServerWebExchange exchange, ValueOperations<String, UserDetails> ops, String token, String username) {
        return webClientBuilder.build()
                .get()
                .uri("http://USER-SERVICE/api/users/username/" + username)
                .retrieve()
                .onStatus(httpStatus -> httpStatus.isError(),
                        clientResponse -> {
                            logger.error("User not found for username: {}", username);
                            return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
                        })
                .bodyToMono(User.class)
                .flatMap(user -> {
                    UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                            user.getUser_name(), user.getPassword(), mapRolesToAuthorities(user.getRoles())
                    );

                    // Cache validated user details in Redis
                    ops.set(token, userDetails, jwtUtil.getExpirationTimeInMillis(token), TimeUnit.MILLISECONDS);
                    logger.info("Cached user details for token: {}", token);

                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities()
                    );

                    return chain.filter(exchange)
                            .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authenticationToken));
                });
    }

    /**
     * Configuration class for the filter.
     */

    public static class Config {
    }
    /**
     * Maps user roles to granted authorities for Spring Security.
     *
     * @param roles a set of roles associated with the user
     * @return a collection of granted authorities
     */
    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Set<Role> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getRoletype()))
                .collect(Collectors.toList());
    }
    private Mono<Void> refreshAccessToken(String refreshToken, GatewayFilterChain chain, ServerWebExchange exchange) {
        try {
            String newAccessToken = jwtUtil.refreshAccessToken(refreshToken);
            // Add new access token to the response header
            exchange.getResponse().getHeaders().add(HttpHeaders.AUTHORIZATION, "Bearer " + newAccessToken);
            return chain.filter(exchange);
        } catch (Exception e) {
            logger.error("Failed to refresh access token: " + e.getMessage());
            return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token"));
        }
    }
}
