package com.example.demo.library.security.configurer.standard;

import java.util.Arrays;
import java.util.List;

import org.jspecify.annotations.Nullable;
import org.springframework.boot.security.autoconfigure.actuate.web.servlet.EndpointRequest;
import org.springframework.boot.security.autoconfigure.web.servlet.PathRequest;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import com.example.demo.library.security.configurer.HttpSecurityCustomizeUtil;

public class StandardConfiguration {

    private @Nullable RequestMatcher bookmarkAwareEntryPointMatcher;

    /**
     * タブ閉じ → セッションタイムアウト → 再アクセス を行ったとき、
     * セッションタイムアウトではなくログイン画面へのリダイレクトを実行するパスパターン。
     * 
     * ブラウザ起動 → 初回アクセスの場合は、この設定に関係なく全てログイン画面にリダイレクトします
     *
     * @param patterns システムの初回アクセスを想定するパスパターン
     */
    public void bookmarkAwareEntryPointMatchers(String... patterns) {
        List<RequestMatcher> matchers = Arrays.asList(patterns).stream()
                .map(pattern -> (RequestMatcher) PathPatternRequestMatcher.withDefaults().matcher(pattern))
                .toList();
        this.bookmarkAwareEntryPointMatcher = new OrRequestMatcher(matchers);
    }

    public void applyDefaults(HttpSecurity http) {
        http.logout(customizer -> {
            customizer.deleteCookies(HttpSecurityCustomizeUtil.createDeleteCookies(http));
        });
        http.csrf(customizer -> {
            customizer.spa();
        });
        http.exceptionHandling(customizer -> {
            UnauthenticatedAuthenticationEntryPoint entryPoint = new UnauthenticatedAuthenticationEntryPoint();
            RequestMatcher matcher = new UnauthenticatedRequestMatcher(bookmarkAwareEntryPointMatcher);
            customizer.defaultAuthenticationEntryPointFor(entryPoint, matcher);
            customizer.accessDeniedHandler(new CsrfAwareAccessDeniedHandler());
        });
    }

    public RequestMatcher defaultPublicEndpoints() {
        return new OrRequestMatcher(
                PathPatternRequestMatcher.withDefaults().matcher("/**"),
                PathRequest.toStaticResources().atCommonLocations(),
                EndpointRequest.toAnyEndpoint());
    }

    public void permitAll(
            AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry registry) {
        registry.requestMatchers("/error/**").permitAll();
        // /css/**, /js/**, /images/**, /webjars/**, /favicon.*, /*/icon-*, /fonts/**
        registry.requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll();
        registry.requestMatchers(EndpointRequest.toAnyEndpoint()).permitAll();
    }

}
