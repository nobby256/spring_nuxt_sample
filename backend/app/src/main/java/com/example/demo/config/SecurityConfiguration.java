package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.library.security.UnifiedWebSecurity;

import io.swagger.v3.oas.annotations.responses.ApiResponse;

/**
 * SpringSecurityのConfigurationクラス。
 */
@Configuration
public class SecurityConfiguration {

	/**
	 * {@link SecurityFilterChain}を作成する。
	 *
	 * @param http {@link HttpSecurity}
	 * @param unified {@link UnifiedWebSecurity}
	 * @return {@link SecurityFilterChain}
	 */
	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http, UnifiedWebSecurity unified) {

		unified.login(http, customizer -> {
			customizer.defaultSuccessUrl("/");
		});
		http.authorizeHttpRequests(customizer -> {
			customizer.requestMatchers(unified.publicEndpoints()).permitAll();
			// customizer.anyRequest().authenticated();
			customizer.anyRequest().permitAll();
		});
		http.csrf(customizer -> {
			customizer.ignoringRequestMatchers(unified.publicEndpoints());
		});
		http.logout(customizer -> {
			customizer.logoutUrl("/api/logout").permitAll();
			customizer.logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.NO_CONTENT));
		});
		unified.applyDefaults(http);

		return http.build();
	}

	@RestController
	static class SecurityController {
		@PostMapping("/api/logout")
		@ApiResponse(responseCode = "204")
		void logout() {
		}
	}

}
