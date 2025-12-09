package com.example.demo.library.security;

import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.util.matcher.RequestMatcher;

import com.example.demo.library.security.configurer.basic.BasicConfigurer;
import com.example.demo.library.security.configurer.login.LoginConfigurer;
import com.example.demo.library.security.configurer.login.SsoFormLoginConfigurer;

public class HttpSecurityDefaults {

    BasicConfigurer configuration = new BasicConfigurer();

    public HttpSecurityDefaults login(HttpSecurity http, Customizer<LoginConfigurer> customizer) {
        LoginConfigurer configurer = new SsoFormLoginConfigurer(http);
        customizer.customize(configurer);
        return this;
    }

    public HttpSecurityDefaults apply(HttpSecurity http) {
        configuration.applyDefaults(http);
        return this;
    }

    public RequestMatcher publicEndpoints() {
        return configuration.defaultPublicEndpoints();
    }

}
