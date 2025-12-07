package com.example.demo.library.spa;

import static org.springframework.web.servlet.function.RequestPredicates.GET;
import static org.springframework.web.servlet.function.RouterFunctions.route;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.MediaType;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.servlet.function.RequestPredicate;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

public class AuthSessionRouterFunction {

    public static Optional<RouterFunction<ServerResponse>> create(SpaConfigurationProperties spaProperties) {
        RequestPredicate predicate = GET("/api/auth-session");// .and(accept(MediaType.APPLICATION_JSON));
        RouterFunction<ServerResponse> function = route(predicate, new AuthSessionRouterFunction()::handle);
        return Optional.of(function);
    }

    ServerResponse handle(ServerRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = authentication.isAuthenticated();
        String name = authentication instanceof AnonymousAuthenticationToken ? "anonymous" : authentication.getName();
        List<String> authorities = authentication.getAuthorities().stream().map(it -> it.getAuthority()).toList();

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