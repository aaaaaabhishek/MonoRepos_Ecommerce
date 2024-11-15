package com.AuthService.Controller;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

@Component
public class RouteValidator {

    // Define the open API endpoints that do not require authentication
    public static final List<String> openApiEndpoints = List.of(
            "/api/auth/signup",  // Updated to include signup endpoint
            "/api/auth/signin",  // Updated to include signin endpoint
            "/eureka"            // Keep the eureka endpoint as open
    );

    public Predicate<ServerHttpRequest> isSecured =
            request -> openApiEndpoints
                    .stream()
                    .noneMatch(uri -> request.getURI().getPath().contains(uri));

}
