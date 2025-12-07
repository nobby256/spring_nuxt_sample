package com.example.demo.library.security.configurer.ignoreurl;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

public class IgnoreUrlConfigurer extends AbstractHttpConfigurer<IgnoreUrlConfigurer, HttpSecurity> {

    private boolean ignoreStaticResource = true;
    private boolean ignoreActuator = true;

    public void ignoreStaticResource(boolean ignoreStaticResource) {
        this.ignoreStaticResource = ignoreStaticResource;
    }

    public void ignoreActuator(boolean ignoreActuator) {
        this.ignoreActuator = ignoreActuator;
    }

    @Override
    public void init(HttpSecurity http) {
    }

    @Override
    public void configure(HttpSecurity http) {
        // 静的リソースとアクチュエーターをSpringSecurityの対象外にする
        IgnoreStaticResourcesAndActuatorWebSecurityCustomizer.registerSingleton(http);
    }

}
