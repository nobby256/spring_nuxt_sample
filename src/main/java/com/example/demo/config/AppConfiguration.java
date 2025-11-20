package com.example.demo.config;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.csrf.CsrfTokenRequestHandler;
import org.springframework.security.web.csrf.InvalidCsrfTokenException;
import org.springframework.security.web.csrf.MissingCsrfTokenException;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.util.WebUtils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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
			}).logout(customizer -> {
				customizer.logoutUrl("/api/logout");
				customizer.logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.NO_CONTENT));
				// セッションクッキーの破棄
				customizer.deleteCookies(createDeleteCookies(http));
				// 必要に応じて有効化
				// customizer.addLogoutHandler(new HeaderWriterLogoutHandler(new
				// ClearSiteDataHeaderWriter(Directive.ALL)));
			}).authorizeHttpRequests(customizer -> {
				// 静的リソースを対象外(/css/**, /js/**, /images/**, /webjars/**, /favicon.*, /*/icon-*)
				customizer.requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll();
				// Actuatorは対象外
				customizer.requestMatchers(EndpointRequest.toAnyEndpoint()).permitAll();
				// エラーコントローラは対象外
				customizer.requestMatchers(PathPatternRequestMatcher.withDefaults().matcher("/error/**")).permitAll();
				customizer.anyRequest().authenticated();
			}).sessionManagement(customizer -> {
				// URLリライティングを有効にする（scfw-multisession利用時に必要）
				customizer.enableSessionUrlRewriting(true);
			}).exceptionHandling(customizer -> {
				// 認可例外発生時のハンドラ設定

				// 【認証済み】
				// AccessDeniedException用のハンドラを登録
				// CSRFトークン関連のエラーか、認証済みだが権限が足りかなった場合に呼び出される
				customizer.accessDeniedHandler(new SsoAccessDeniedHandler());

				// 【未認証】
				// REST向けのAuthenticationEntryPointを追加
				// やりたい事は下記の通り。
				//
				// authenticationEntryPointは使用しない。
				// つまりnullを維持。この値がnot nullだとDefaultLoginPageGeneratingFilterが登録されなくなる。
				// ただし、ユーザーによってauthenticationEntryPointにハンドラが設定されるのは止めない（止めようがない）
				//
				// やりたい事は下記の優先度になるAuthenticationEntryPointを作成すること。
				//
				// 優先度高：LoginConfigurerが登録するLoginUrlAuthenticationEntryPoint
				// 優先度中：ユーザがdefaultAuthenticationEntryPointFor()で登録したハンドラ
				// 優先度低：今回ここで登録するハンドラ（REST用を想定。Forbidden(403)）
				//
				// これを実現する為には条件が２つ。
				// 1. 最も早く（LoginConfigurer.init()、及びユーザーより先）登録する事
				// 2. defaultAuthenticationEntryPointFor()の第二引数に`()->false`を指定する事。
				// 上記の条件を満たすことで、ExceptionHandlingConfigurer#createDefaultEntryPoint()が
				// 期待どおりのAuthenticationEntryPointを作成してくれる。
				customizer.defaultAuthenticationEntryPointFor(new SsoAuthenticationEntryPoint(), request -> false);
			}).csrf(customizer -> {
				// Actuatorは対象外
				customizer.ignoringRequestMatchers(EndpointRequest.toAnyEndpoint());
				// エラーコントローラは対象外
				customizer.ignoringRequestMatchers(PathPatternRequestMatcher.withDefaults().matcher("/error/**"));

				// クッキーを使用したCSRFリポジトリを使用する（httpOnly=false）
				CookieCsrfTokenRepository repository = CookieCsrfTokenRepository.withHttpOnlyFalse();
				repository.setCookieCustomizer(cookieCustomizer -> {
					// server.servlet.session.cookie.secureと設定を連動させる（https時にはsecure=trueを推奨）
					Boolean secure = serverProperties.getServlet().getSession().getCookie().getSecure();
					cookieCustomizer.secure(secure == null ? false : secure);
				});
				customizer.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());

				// SPA用のカスタムハンドラを登録する
				customizer.csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler());
			});

			return http.build();
		}

		/**
		 * ログアウト時に削除するクッキー名の配列にセッションIDのクッキー名を追加する。
		 *
		 * @param http    {@link HttpSecurity}
		 * @param cookies クッキー名
		 * @return クッキーの配列
		 */
		String[] createDeleteCookies(HttpSecurity http, String... cookies) {
			String sessionCookie = sessionCookieName(http);
			String[] combineCookies;
			if (cookies != null) {
				combineCookies = new String[cookies.length + 1];
				combineCookies[0] = sessionCookie;
				System.arraycopy(cookies, 0, combineCookies, 1, cookies.length);
			} else {
				combineCookies = new String[] { sessionCookie };
			}
			return combineCookies;
		}

		/**
		 * セッションIDのクッキー名を取得する。
		 *
		 * @param http {@link HttpSecurity}
		 * @return セッションIDのクッキー名
		 */
		String sessionCookieName(HttpSecurity http) {
			ServerProperties properties = SsoBeanUtil.getBeanOrNull(http, ServerProperties.class);
			Objects.requireNonNull(properties);
			String cookieName = properties.getServlet().getSession().getCookie().getName();
			if (cookieName == null) {
				cookieName = "JSESSIONID";
			}
			return cookieName;
		}

		/**
		 * 認証済み状態で認可エラーが発生したときのハンドラクラス。
		 */
		public static class SsoAccessDeniedHandler implements AccessDeniedHandler {
			/** {@link Logger}。 */
			protected static final Logger logger = LoggerFactory.getLogger(SsoAccessDeniedHandler.class);

			@Override
			public void handle(HttpServletRequest request, HttpServletResponse response,
					AccessDeniedException accessDeniedException) throws IOException, ServletException {
				if (response.isCommitted()) {
					logger.trace("Did not write to response since already committed");
					return;
				}

				// ハンドラの呼び出された理由によって振る舞いを変更する
				if (accessDeniedException instanceof MissingCsrfTokenException) {
					// クライアントがCSRFトークンを送信してきたが、セッションにCSRFトークンが格納されていなかったケース。
					// CsrfFilterによって検出される。
					// 正しい処理フローならばPOST要求を行う前のGET要求で、必ずCSRFトークンがセッションに格納されているはず。
					// それでもセッションにCSRFトークンが格納されていない理由は、セッションタイムアウトが発生したことで削除された可能性がある。
					// なお、正しい処理フローを行ってない場合（初回アクセスがPOST要求である場合など）は、このエラーが発生する。
					// セッションタイムアウトか間違ったフローだったのかを判断したいところではあるが、判断のつけようがない。
					logger.debug("Responding with 401 status code");
					response.sendError(HttpStatus.UNAUTHORIZED.value());
				} else if (accessDeniedException instanceof InvalidCsrfTokenException) {
					// クライアント側のCSRFトークンに問題があったケース（送信してこなかった/間違った値だった）。
					// CsrfFilterによって検出される。
					logger.debug("Responding with 403 status code");
					response.sendError(HttpStatus.FORBIDDEN.value());
				} else {
					// 認証済み状態で認可エラーが発生したケース。
					// ExceptionTranslationFilterから呼び出される。
					// なお、未認証状態で認可エラーが発生した場合は、このハンドラではなくAuthenticationEntryPointが呼び出される。
					logger.debug("Responding with 403 status code");
					response.sendError(HttpStatus.FORBIDDEN.value());
				}
				// ErrorControllerで例外を取得できるようにする為に追加
				request.setAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE, accessDeniedException);
			}
		}

		/**
		 * 未認証状態で認可エラーが発生したときのハンドラクラス。
		 * <p>
		 * scfw-security-sso利用時のAuthenticationEntryPointはDelegatingAuthenticationEntryPointを使用することで、
		 * html要求（AJAXのぞく）と、その他要求（主にRESTとAJAX）で、別々のハンドラで対処をする。
		 * <br/>
		 * http要求時（AJAX除く）のハンドラはFormLoginConfigurer、OAuth2LoginConfigurer、Saml2LoginConfigurerが登録するハンドラが担当する。
		 * このハンドラクラスはその他要求用（主にRESTとAJAX）のハンドラである。
		 * <br/>
		 * また、REST要求において認証が必要なエンドポイントに対して、未認証状態でアクセスすることはバグか攻撃かのいずれかである。
		 * 特に本番環境を前提にした場合は攻撃の可能性が高いと判断し、スタックトレースは出力しない。
		 * 
		 * このハンドラはREST要求時用であるため、リダイレクトは行わない。<br/>
		 * html要求以外ということもあって、エラー検出時にhtml画面ではなくステータスコードでクライアントに伝える事になる。
		 * </p>
		 */
		public static class SsoAuthenticationEntryPoint implements AuthenticationEntryPoint {
			/** {@link Logger}。 */
			protected static final Logger logger = LoggerFactory.getLogger(SsoAuthenticationEntryPoint.class);

			@Override
			public void commence(HttpServletRequest request, HttpServletResponse response,
					AuthenticationException authException) throws IOException, ServletException {
				if (response.isCommitted()) {
					logger.trace("Did not write to response since already committed");
					return;
				}
				// 本来であれば、スタックトレースが不要、かつ、クライアントに対してエラーを伝える方法がステータスコードに限定できるならば、
				// /errorに折り返す必要は無く、setStatus()の利用で事足りる。
				// しかし、他のエラーハンドはすべてErrorControllerを利用しているため、統一感を高めるために/errorへ折り返すことにする。
				// スタックトレースの出力は不要である為、例外ではなくsendErrorを使用する。
				// REST呼び出しにおける未認証時の認可エラーは攻撃である可能性が高いため、FORBIDDEN(403)を返す
				logger.debug("Responding with 403 status code");
				response.sendError(HttpStatus.FORBIDDEN.value());
				request.setAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE, authException);
			}
		}

		/**
		 * MPA/SPA両対応の{@link CsrfTokenRequestHandler}。
		 * <p>
		 * SpringSecurityのドキュメントで提示されているコードと同じクラスです。<br/>
		 * https://docs.spring.io/spring-security/reference/servlet/exploits/csrf.html#csrf-integration-javascript-spa
		 * </p>
		 */
		static class SpaCsrfTokenRequestHandler implements CsrfTokenRequestHandler {
			/** リクエストヘッダ用のハンドラ。 */
			private final CsrfTokenRequestHandler plain = new CsrfTokenRequestAttributeHandler();
			/** リクエストパラメータ用のハンドラ。 */
			private final CsrfTokenRequestHandler xor = new XorCsrfTokenRequestAttributeHandler();

			@Override
			public void handle(HttpServletRequest request, HttpServletResponse response,
					Supplier<CsrfToken> csrfToken) {
				// hiddenタグ、metaタグが使用するXOR化したトークン値（BREACH対策）をリクエスト毎に準備します。
				xor.handle(request, response, csrfToken);

				// CSRFトークンはcsrfToken.get()を呼び出したときに発行されますが（無かった時のみジェネレート）、
				// このメソッドは標準構成ではformタグ、metaタグのレンダリング時のみ呼び出されます。
				// つまりHTMLを動的にレンダリングする機会がないシステムはCSRFトークンを発行する機会が無いことになります。
				// この典型例がOAuth2,SAML2認証のSPAです（FORM認証はログイン画面でformタグをレンダリングします）。
				// この問題に対応する為に、このハンドラはリクエストの度にトークン発行（無かった時のみジェネレート）を行います。
				// また、引数のcsrfTokenがnullだった場合、xor.handle()ではトークン削除、csrfToken.get()ではトークン新発行が行われます。
				// この一連の流れによってJSESSIONID同様のログイン/ログアウト時の洗い替えも実現します。
				csrfToken.get();
			}

			@Override
			public String resolveCsrfTokenValue(HttpServletRequest request, CsrfToken csrfToken) {
				// 最初にリクエストヘッダーのトークン（素の値）を確認し、無ければパラメーターのトークン（XOR化された値）を確認します
				String headerValue = request.getHeader(csrfToken.getHeaderName());
				return (StringUtils.hasText(headerValue) ? this.plain : this.xor).resolveCsrfTokenValue(request,
						csrfToken);
			}
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
