package com.example.demo.library.security.configurer.basic;

import org.springframework.boot.security.autoconfigure.actuate.web.servlet.EndpointRequest;
import org.springframework.boot.security.autoconfigure.web.servlet.PathRequest;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import com.example.demo.library.security.configurer.HttpSecurityCustomizeUtil;

public class BasicConfigurer {

    public void applyDefaults(HttpSecurity http) {
        http.logout(customizer -> {
            customizer.deleteCookies(HttpSecurityCustomizeUtil.createDeleteCookies(http));
        });
        http.csrf(customizer -> {
            customizer.spa();
        });
        http.exceptionHandling(customizer -> {
            UnauthenticatedAuthenticationEntryPoint entryPoint = new UnauthenticatedAuthenticationEntryPoint();
            RequestMatcher matcher = new UnauthenticatedRequestMatcher(null);
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

}
