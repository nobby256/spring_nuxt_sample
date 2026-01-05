package com.example.demo.library.security.configurer.defaults;

import java.util.Collections;
import java.util.Locale;

import jakarta.servlet.http.HttpServletRequest;

import org.jspecify.annotations.Nullable;
import org.springframework.http.MediaType;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestHeaderRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

public class UnauthenticatedRequestMatcher implements RequestMatcher {

	/** UNAUTHORIZED判定を行うMatcher。 */
	private final RequestMatcher matcher;

	/**
	 * コンストラクタ。
	 *
	 * @param forceRedirectMatcher タイムアウトを検出した時にログイン画面にリダイレクトするURLを判定するMatcher
	 */
	public UnauthenticatedRequestMatcher(@Nullable RequestMatcher forceRedirectMatcher) {
		// REST or AJAX
		RequestMatcher restOrAjaxMatcher = getRestOrAjaxRequestMatcher();
		// セッションタイムアウト
		RequestMatcher forceMatcher = forceRedirectMatcher != null
				? forceRedirectMatcher
				: PathPatternRequestMatcher.withDefaults().matcher("/**");
		RequestMatcher timeoutMatcher = getSessionTimeoutRequestMatcher(forceMatcher);
		// UNAUTHORIZED判定
		this.matcher = new OrRequestMatcher(restOrAjaxMatcher, timeoutMatcher);
	}

	@Override
	public boolean matches(HttpServletRequest request) {
		return matcher.matches(request);
	}

	/**
	 * セッションタイムアウトを判定するMatcherを取得する。
	 *
	 * @param forceRedirectMatcher タイムアウトを検出した時にログイン画面にリダイレクトするURLを判定するMatcher
	 * @return セッションタイムアウトを判定するMatcher
	 */
	private RequestMatcher getSessionTimeoutRequestMatcher(RequestMatcher forceRedirectMatcher) {
		return (request) -> {
			if (request.getRequestedSessionId() == null) { // セッションIDを受信してない？
				// ブラウザ起動 → 初回アクセスと判断
				return false; // セッションタイムアウトではない
			} else if (!request.isRequestedSessionIdValid()) { // セッションIDを受信、かつ、IDが無効？
				// 利用中のタイムアウト、もしくは、タブ閉じ → （タイムアウト） → 再アクセス
				if (request.getMethod().toUpperCase(Locale.ROOT).equals("GET")) {
					if (forceRedirectMatcher.matches(request)) { // ブックマーク想定URLか？
						return false; // セッションタイムアウトではない
					}
				}
			}
			return true; // セッションタイムアウト
		};
	}

	/**
	 * RESTもしくはAJAX要求を判定するMatcherを取得する。
	 *
	 * @return RESTもしくはAJAX要求を判定するMatcher
	 */
	private RequestMatcher getRestOrAjaxRequestMatcher() {
		// HTML要求
		MediaTypeRequestMatcher htmlMatcher = new MediaTypeRequestMatcher(MediaType.TEXT_HTML);
		htmlMatcher.setIgnoredMediaTypes(Collections.singleton(MediaType.ALL)); // */* は許容しない
		// REST（非HTML要求）
		RequestMatcher notHtmlMatcher = new NegatedRequestMatcher(htmlMatcher);
		// AJAX
		RequestMatcher ajaxMatcher = new RequestHeaderRequestMatcher("X-Requested-With", "XMLHttpRequest");
		return new OrRequestMatcher(notHtmlMatcher, ajaxMatcher);
	}
}
