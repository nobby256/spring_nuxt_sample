package com.example.demo.library.security.autoconfigure;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;

import com.example.demo.library.security.HttpSecurityDefaults;

@AutoConfiguration
@ConditionalOnWebApplication
public class SecurityAutoConfiguration {

    @Bean
    HttpSecurityDefaults httpSecurityDefaults() {
        return new HttpSecurityDefaults();
    }

}
