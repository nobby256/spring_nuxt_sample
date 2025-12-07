package com.example.demo.library.security.configurer.standard;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.security.autoconfigure.actuate.web.servlet.EndpointRequest;
import org.springframework.boot.security.autoconfigure.web.servlet.PathRequest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.util.Assert;

import com.example.demo.library.security.configurer.HttpSecurityCustomizeUtil;

public class IgnoreStaticResourcesAndActuatorWebSecurityCustomizer implements WebSecurityCustomizer {
    private boolean ignoreStaticResources;
    private boolean ignoreActuators;

    private IgnoreStaticResourcesAndActuatorWebSecurityCustomizer(boolean ignoreStaticResources,
            boolean ignoreActuators) {
        this.ignoreStaticResources = ignoreStaticResources;
        this.ignoreActuators = ignoreActuators;
    }

    @Override
    public void customize(WebSecurity web) {
        if (ignoreStaticResources) {
            // 静的リソースを対象外(/css/**, /js/**, /images/**, /webjars/**, /favicon.*, /*/icon-*)
            web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
        }
        if (ignoreActuators) {
            // Actuatorは対象外
            web.ignoring().requestMatchers(EndpointRequest.toAnyEndpoint());
        }
    }

    public static void registerSingleton(HttpSecurity http, boolean ignoreStaticResources, boolean ignoreActuators) {
        ApplicationContext context = HttpSecurityCustomizeUtil.getSharedOrBean(http, ApplicationContext.class);
        Assert.state(context != null, "context must not be null.");
        if (context instanceof GenericApplicationContext ctx) {
            IgnoreStaticResourcesAndActuatorWebSecurityCustomizer instance = new IgnoreStaticResourcesAndActuatorWebSecurityCustomizer(
                    ignoreStaticResources, ignoreActuators);
            ConfigurableListableBeanFactory beanFactory = ctx.getBeanFactory();
            beanFactory.registerSingleton("ignoreStaticResourcesAndActuatorWebSecurityCustomizer", instance);
        }
    }

}
