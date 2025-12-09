package com.example.demo.library.security.autoconfigure;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;

import com.example.demo.library.security.UnifiedWebSecurity;
import com.example.demo.library.security.configurer.login.UnifiedFormLoginConfigurer;
import com.example.demo.library.security.configurer.login.UnifiedLoginConfigurer;

@AutoConfiguration
@ConditionalOnWebApplication
public class SecurityAutoConfiguration {

    @Bean
    UnifiedWebSecurity unifiedWebSecurity() {
        UnifiedLoginConfigurer loginConfigurer = new UnifiedFormLoginConfigurer();
        return new UnifiedWebSecurity(loginConfigurer);
    }

}
