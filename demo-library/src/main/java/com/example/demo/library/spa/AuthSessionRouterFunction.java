package com.example.demo.library.spa;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.servlet.function.HandlerFunction;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

public class AuthSessionRouterFunction {

    public static RouterFunction<ServerResponse> create(SpaConfigurationProperties spaProperties) {
        return RouterFunctions.route().path("/api/auth-session",
                (RouterFunctions.Builder builder) -> {
                    builder.GET(new GetHandler());
                }).build();
    }

    public static class GetHandler implements HandlerFunction<ServerResponse> {
        @Override
        public ServerResponse handle(ServerRequest request) {
            boolean isAuthenticated = false;
            String name = "anonymous";
            List<String> authorities = List.of();

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null) {
                isAuthenticated = authentication.isAuthenticated();
                if (authentication instanceof AnonymousAuthenticationToken) {
                    name = authentication.getName();
                }
                authorities = authentication.getAuthorities().stream().map(it -> it.getAuthority()).toList();
            }

            CsrfToken csrfToken = (CsrfToken) request.attribute(CsrfToken.class.getName()).orElseThrow();
            String token = csrfToken != null ? csrfToken.getToken() : null;

            Map<String, Object> body = new HashMap<>();
            body.put("user", name);
            body.put("authorities", authorities);
            body.put("isAuthenticated", isAuthenticated);
            body.put("csrfParameterToken", token);
            body.put("csrfParameterName", csrfToken.getParameterName());

            return ServerResponse
                    .ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body);
        }
    }

}