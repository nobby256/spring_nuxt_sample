package com.example.demo.library.security.configurer.defaults.security6;

import java.util.function.Supplier;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.jspecify.annotations.Nullable;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.csrf.CsrfTokenRequestHandler;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;
import org.springframework.util.StringUtils;

/**
 * MPA/SPA両対応の{@link CsrfTokenRequestHandler}。
 * <p>
 * SpringSecurityのドキュメントで提示されているコードと同じクラスです。<br/>
 * https://docs.spring.io/spring-security/reference/servlet/exploits/csrf.html#csrf-integration-javascript-spa
 * </p>
 */
public class SpaCompatibleCsrfTokenRequestHandler implements CsrfTokenRequestHandler {
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
		@SuppressWarnings("unused")
		CsrfToken unused = csrfToken.get();
	}

	@Override
	public @Nullable String resolveCsrfTokenValue(HttpServletRequest request, CsrfToken csrfToken) {
		// 最初にリクエストヘッダーのトークン（素の値）を確認し、無ければパラメーターのトークン（XOR化された値）を確認します
		String headerValue = request.getHeader(csrfToken.getHeaderName());
		return (StringUtils.hasText(headerValue) ? this.plain : this.xor).resolveCsrfTokenValue(request,
						csrfToken);
	}
}
