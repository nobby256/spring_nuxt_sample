package com.example.demo.library.security;

import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import com.example.demo.library.security.configurer.exceptionhandling.ExceptionHandlingConfigurer;
import com.example.demo.library.security.configurer.ignoreurl.IgnoreUrlConfigurer;
import com.example.demo.library.security.configurer.standard.StandardConfigurer;

public class HttpSecuritySettings {
    public static HttpSecurity apply(HttpSecurity http, Customizer<StandardConfigurer> customizer) {
        return http.with(new StandardConfigurer(), customizer);
    }

    public static HttpSecurity applyDefaults(HttpSecurity http) {
        applyExceptionHandling(http, Customizer.withDefaults());
        applyIgnoringUrl(http, Customizer.withDefaults());
        return http;
    }

    public static HttpSecurity applyExceptionHandling(HttpSecurity http,
            Customizer<ExceptionHandlingConfigurer> customizer) {
        return http.with(new ExceptionHandlingConfigurer(), customizer);
    }

    public static HttpSecurity applyIgnoringUrl(HttpSecurity http, Customizer<IgnoreUrlConfigurer> customizer) {
        return http.with(new IgnoreUrlConfigurer(), customizer);
    }
}
