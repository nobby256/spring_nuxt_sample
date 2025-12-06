package com.example.demo.library.security.autoconfigure;

import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;

@AutoConfiguration
@ConditionalOnWebApplication
public class SecurityAutoConfiguration {

    WebSecurityCustomizer ignoreWebSecurityCustomizer(){
        return (WebSecurity web) -> {
            // 静的リソースを対象外(/css/**, /js/**, /images/**, /webjars/**, /favicon.*, /*/icon-*)
            web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
            // Actuatorは対象外
            web.ignoring().requestMatchers(EndpointRequest.toAnyEndpoint());
        };
    }

}
