package com.example.demo.library.security.configurer.ignoreurl;

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

    private IgnoreStaticResourcesAndActuatorWebSecurityCustomizer() {
    }

    @Override
    public void customize(WebSecurity web) {
        // 静的リソースを対象外(/css/**, /js/**, /images/**, /webjars/**, /favicon.*, /*/icon-*)
        web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
        // Actuatorは対象外
        web.ignoring().requestMatchers(EndpointRequest.toAnyEndpoint());
    }

    public static void registerSingleton(HttpSecurity http) {
        ApplicationContext context = HttpSecurityCustomizeUtil.getSharedOrBean(http, ApplicationContext.class);
        Assert.state(context != null, "context must not be null.");
        if (context instanceof GenericApplicationContext ctx) {
            IgnoreStaticResourcesAndActuatorWebSecurityCustomizer instance = new IgnoreStaticResourcesAndActuatorWebSecurityCustomizer();
            ConfigurableListableBeanFactory beanFactory = ctx.getBeanFactory();
            beanFactory.registerSingleton("ignoreStaticResourcesAndActuatorWebSecurityCustomizer", instance);
        }
    }

}
