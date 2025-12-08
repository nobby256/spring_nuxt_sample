package com.example.demo.library.security.configurer.standard;

import java.lang.reflect.Field;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.Arrays;
import java.util.List;

import org.jspecify.annotations.Nullable;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.security.autoconfigure.actuate.web.servlet.EndpointRequest;
import org.springframework.boot.security.autoconfigure.web.servlet.PathRequest;
import org.springframework.security.config.annotation.web.AbstractRequestMatcherRegistry;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer.AuthorizationManagerRequestMatcherRegistry;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.ReflectionUtils;

import com.example.demo.library.security.configurer.HttpSecurityCustomizeUtil;

public class StandardConfigurer extends AbstractHttpConfigurer<StandardConfigurer, HttpSecurity> {

    private @Nullable RequestMatcher bookmarkAwareEntryPointMatcher;
    private boolean errorPagePermitAll = true;
    private boolean staticResourcesPermitAll = true;
    private boolean actuatorsPermitAll = true;

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

    public void errorPagePermitAll(boolean permitAll) {
        this.errorPagePermitAll = permitAll;
    }

    /**
     * 静的リソースを認証不要にする。
     * 
     * @param permitAll true:認証不要にする
     * @see {@link StaticResourceLocation)
     */
    public void staticResourcesPermitAll(boolean permitAll) {
        this.staticResourcesPermitAll = permitAll;
    }

    /**
     * アクチュエーター（/actuator）を認証不要にする。
     * 
     * @param permitAll true:認証不要にする
     */
    public void actuatorsPermitAll(boolean permitAll) {
        this.actuatorsPermitAll = permitAll;
    }

    @Override
    public void setBuilder(HttpSecurity http) {
        super.setBuilder(http);

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

        if (errorPagePermitAll) {
            WebProperties properties = HttpSecurityCustomizeUtil.getBeanOrNull(http, WebProperties.class);
            String path = properties.getError().getPath();
            String pattern = path + "/**";
            http.authorizeHttpRequests(customizer -> {
                customizer.requestMatchers(pattern).permitAll();
            });
            http.csrf(customizer -> {
                customizer.ignoringRequestMatchers(pattern);
            });
        }

        http.authorizeHttpRequests(customizer -> {
            if (staticResourcesPermitAll) {
                // /css/**, /js/**, /images/**, /webjars/**, /favicon.*, /*/icon-*, /fonts/**
                customizer.requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll();
            }
            if (actuatorsPermitAll) {
                customizer.requestMatchers(EndpointRequest.toAnyEndpoint()).permitAll();
            }
        });
    }

    @Override
    public void init(HttpSecurity http) {
        http.authorizeHttpRequests(customizer -> {
            if (!getAnyRequests(customizer)) {
                customizer.anyRequest().permitAll();
            }
        });
    }

    @Override
    public void configure(HttpSecurity http) {
    }

    boolean getAnyRequests(@SuppressWarnings("rawtypes") AuthorizationManagerRequestMatcherRegistry registry) {
        Field field;
        try {
            field = ReflectionUtils.findField(AbstractRequestMatcherRegistry.class, "anyRequestConfigured");
        } catch (Exception ex) {
            throw new UndeclaredThrowableException(ex);
        }
        ReflectionUtils.makeAccessible(field);
        return (boolean) ReflectionUtils.getField(field, registry);
    }

}
