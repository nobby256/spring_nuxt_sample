package com.example.demo.library.spa;

import static org.springframework.web.servlet.function.RequestPredicates.GET;
import static org.springframework.web.servlet.function.RequestPredicates.accept;
import static org.springframework.web.servlet.function.RouterFunctions.route;

import java.time.Duration;
import java.time.Instant;
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

import jakarta.servlet.http.HttpSession;

public class AuthenticationRouterFunction {

    public static Optional<RouterFunction<ServerResponse>> create(SpaConfigurationProperties spaProperties) {
        RequestPredicate predicate = GET("/api/session-context").and(accept(MediaType.APPLICATION_JSON));
        RouterFunction<ServerResponse> function = route(predicate, new AuthenticationRouterFunction()::handle);
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
        body.put("name", name);
        body.put("authorities", authorities);
        body.put("isAuthenticated", isAuthenticated);
        body.put("csrf", token);

        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(body);
    }

    private String getSessionExpiresAt(ServerRequest request, Duration sessionTimeout) {
        HttpSession session = request.session();
        // 1. セッションの最終アクセス時刻を取得 (Instant型)
        Instant lastAccessedTime = Instant.ofEpochMilli(session.getLastAccessedTime());

        // 2. セッションのタイムアウト時間を取得 (Duration型)
        // application.properties の server.reactive.session.timeout で設定された値
        java.time.Duration timeout = sessionTimeout;

        // 3. 有効期限を計算 (最終アクセス時刻 + タイムアウト時間)
        Instant expirationInstant = lastAccessedTime.plus(timeout);

        // 4. JavaScriptで解釈可能なISO 8601形式の文字列に変換
        // Instant.toString() はデフォルトで "2024-05-21T10:30:00.123Z" のような形式を返す
        return expirationInstant.toString();
    }

}