package com.example.demo.api;

import java.util.List;
import java.util.Objects;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Builder;
import lombok.Getter;

@RequestMapping("/api/auth-session")
@RestController
public class AuthSessionController {

    @GetMapping
    AuthSession get(HttpServletRequest request) {
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

        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        Objects.requireNonNull(csrfToken);
        String token = csrfToken != null ? csrfToken.getToken() : null;

        return AuthSession.builder()
                .user(name)
                .authorities(authorities)
                .isAuthenticated(isAuthenticated)
                .csrfParameterToken(token)
                .csrfParameterName(csrfToken.getParameterName())
                .build();
    }

    @Getter
    @Builder
    public static class AuthSession {
        private String user;
        private List<String> authorities;
        private boolean isAuthenticated;
        private String csrfParameterToken;
        private String csrfParameterName;
    }

}
