package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

import com.example.demo.library.security.HttpSecuritySettings;

/**
 * アプリケーションの設定クラス。
 */
@Configuration
public class SecurityConfiguration {

	/**
	 * SpringSecurityを設定する。
	 *
	 * @param http {@link HttpSecurity}
	 * @return {@link SecurityFilterChain}
	 * @throws Exception 例外
	 */
	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		// ログイン
		http.formLogin(customizer -> {
			customizer.defaultSuccessUrl("/");
		});
		// ログアウト
		http.logout(customizer -> {
			customizer.logoutUrl("/api/logout").permitAll();
			customizer.logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.NO_CONTENT));
		});
		// 認証
		http.authorizeHttpRequests(customizer -> {
			customizer.requestMatchers("/error/**").permitAll();
			customizer.anyRequest().authenticated();
		});
		// CSRF
		http.csrf(customizer -> {
			customizer.ignoringRequestMatchers("/error/**");
		});

		// 上記以外は標準設定に従う
		HttpSecuritySettings.applyDefaults(http);

		return http.build();
	}

}
