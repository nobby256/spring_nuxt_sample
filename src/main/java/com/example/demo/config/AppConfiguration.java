package com.example.demo.config;

import java.util.Arrays;

import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.example.demo.library.security.HttpSecurityCustomizer;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * アプリケーションの設定クラス。
 */
@Configuration
public class AppConfiguration {

	/**
	 * SpringSecurityの設定。
	 */
	@Configuration
	static class SecurityConfiguration {

		/**
		 * {@link SecurityFilterChain}を作成する。
		 *
		 * @param http             {@link HttpSecurity}
		 * @param serverProperties {@link ServerProperties}
		 * @return {@link SecurityFilterChain}
		 * @throws Exception 例外
		 */
		@Bean
		SecurityFilterChain securityFilterChain(HttpSecurity http, ServerProperties serverProperties) throws Exception {
			http.formLogin(customizer -> {
				// customizer.loginPage("/login");
				customizer.defaultSuccessUrl("/");
			});
			http.logout(customizer -> {
				customizer.logoutUrl("/api/logout");
			});
			HttpSecurityCustomizer.withDefault(http);
			return http.build();
		}

	}

	/**
	 * Nuxtとの連携時の追加設定。
	 */
	@Configuration
	@Profile("nuxt")
	static class NuxtSecurityConfiguration {

		/** Nuxtのオリジン。 */
		private static final String NUXT_URL = "http://localhost:3000";

		/**
		 * Nuxtからのアクセスを許可するCORS設定を登録する。
		 * 
		 * @return {@link CorsConfigurationSource}
		 */
		@Bean("corsConfigurationSource") // ⇐ このbean名が重要
		CorsConfigurationSource corsConfigurationSource() {
			CorsConfiguration corsConfiguration = new CorsConfiguration();
			corsConfiguration.setAllowedOriginPatterns(Arrays.asList(NUXT_URL));
			corsConfiguration.addAllowedMethod(CorsConfiguration.ALL);
			corsConfiguration.addAllowedHeader(CorsConfiguration.ALL);
			corsConfiguration.setAllowCredentials(true);

			UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
			source.registerCorsConfiguration("/**", corsConfiguration);
			return source;
		}

		/**
		 * `/`へのアクセスをNuxtにリダイレクトするViewControllerを登録する。
		 * <p>
		 * SpringBootは`/`要求を`forward:/index.html`に変換する機能をビルドインで提供します（{@code WelcomePageHandlerMapping}）。<br/>
		 * 運用時はこの機能によって、`/resources/public/index.html`が`/`で公開されます。<br/>
		 * しかし、Nuxtと連携するには`/`要求に対して`http://localhost:3000`へのリダイレクトを返す必要があります。<br/>
		 * {@code WelcomePageHandlerMapping}よりも解決の優先度が高い{@code ViewController}を登録することで開発サーバとの連携を行います。
		 * </p>
		 * 
		 * @return {@link WebMvcConfigurer}
		 */
		@Bean
		WebMvcConfigurer nuxtRedirectWebMvcConfigurer() {
			return new WebMvcConfigurer() {
				@Override
				public void addViewControllers(ViewControllerRegistry registry) {
					registry.addRedirectViewController("/", NUXT_URL);
				}
			};
		}
	}

	/**
	 * デフォルトの{@link ObjectMapper}を差し替える。
	 * <p>
	 * レスポンスのJSONの最適化設定を施した{@link ObjectMapper}を作成する。
	 * </p>
	 * 
	 * @return {@link ObjectMapper}
	 */
	@Bean
	ObjectMapper objectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		// NON_ABSENTの挙動は下記の通り
		// null、Optionalはシリアライズしない（undefined）。
		// ただし長さゼロの文字列、Emptyな配列はシリアライズする。
		objectMapper.setSerializationInclusion(JsonInclude.Include.NON_ABSENT);
		return objectMapper;
	}

}
