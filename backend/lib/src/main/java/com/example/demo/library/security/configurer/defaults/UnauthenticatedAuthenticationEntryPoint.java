package com.example.demo.library.security.configurer.defaults;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.web.util.WebUtils;

public class UnauthenticatedAuthenticationEntryPoint implements AuthenticationEntryPoint {

	/** {@link Logger}。 */
	private static final Logger logger = LoggerFactory.getLogger(UnauthenticatedAuthenticationEntryPoint.class);

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
					AuthenticationException authException) throws IOException, ServletException {
		// HTML系の要求とREST系のレスポンスの違いはErrorControllerに任せる。
		// HTMLフラグメントを返すAJAXのレスポンスはカスタムのErrorViewResolver内で対応を想定。
		logger.debug("Responding with 401 status code");
		response.sendError(HttpStatus.UNAUTHORIZED.value());
		// ErrorControllerやErrorViewResolverで例外クラスを取得できるようにする為に例外を格納
		request.setAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE, authException);
	}

}
