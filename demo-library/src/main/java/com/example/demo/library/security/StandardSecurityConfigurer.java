package com.example.demo.library.security;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.util.matcher.RequestMatcher;

import com.example.demo.library.security.configurer.standard.StandardConfiguration;

public class StandardSecurityConfigurer {

    StandardConfiguration configuration = new StandardConfiguration();

    public void applyDefaults(HttpSecurity http) {
        configuration.applyDefaults(http);
    }

    public RequestMatcher defaultPublicEndpoints() {
        return configuration.defaultPublicEndpoints();
    }

}
