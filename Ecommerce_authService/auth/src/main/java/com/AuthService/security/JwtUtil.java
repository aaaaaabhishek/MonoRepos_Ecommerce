package com.AuthService.security;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {
    @Value("${security.jwt.refreshExpirationTime}")
    private Long refreshExpirationTime;

    @Value("${security.jwt.accessExpirationTime}")
    private Long accessExpirationTime;

    public static final String SECRET = "5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437";
    public void validateToken(final String token) {
        Jwts.parserBuilder().setSigningKey(getSignKey()).build().parseClaimsJws(token);
    }
    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    private String getJWTfromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")) {
            return bearerToken.substring(7);
        }
        return null;
    }
    public String getUsernameFromJWT(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }
    // Validate the token by checking if it is expired
    public long getExpirationTimeInMillis(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(token)
                .getBody();
        return claims.getExpiration().getTime();
    }

    public boolean isTokenExpired(String token) {
        return getExpirationTimeInMillis(token) < System.currentTimeMillis();
    }

    public boolean isTokenAboutToExpire(String token, long thresholdMillis) {
        long expirationTime = getExpirationTimeInMillis(token);
        return (expirationTime - System.currentTimeMillis()) < thresholdMillis;
    }

    public String generateRefreshToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + refreshExpirationTime))
                .signWith(SignatureAlgorithm.HS256, SECRET)
                .compact();
    }
    /**
     * Extracts the JWT token from the authorization header.
     *
     * @param authHeader the authorization header containing the token
     * @return the extracted JWT token
     * @throws ResponseStatusException if the authorization header format is invalid
     */
    public String extractToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid authorization header format");
    }
    public String refreshAccessToken(String refreshToken) {
        // Validate the refresh token
        validateToken(refreshToken);

        // Extract the username from the refresh token
        String username = getUsernameFromJWT(refreshToken);

        // Generate a new access token
        return generateAccessToken(username);
    }
    public String generateAccessToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + accessExpirationTime))
                .signWith(SignatureAlgorithm.HS256, getSignKey())
                .compact();
    }
//    public void invalidateToken(String token) {
//        // Invalidate the token by storing a flag in Redis (or other storage)
//        redisTemplate.opsForValue().set(token, "invalid", accessExpirationTime, TimeUnit.MILLISECONDS);
//    }

}
