package com.example.demo.library.security.configurer.defaults;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.csrf.MissingCsrfTokenException;
import org.springframework.web.util.WebUtils;

public class CsrfAwareAccessDeniedHandler implements AccessDeniedHandler {

	protected static final Log logger = LogFactory.getLog(CsrfAwareAccessDeniedHandler.class);

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
					AccessDeniedException accessDeniedException) throws IOException, ServletException {
		if (response.isCommitted()) {
			logger.trace("Did not write to response since already committed");
			return;
		}

		// ErrorControllerやErrorViewResolverで例外クラスを取得できるようにする為に例外を格納
		request.setAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE, accessDeniedException);
		request.setAttribute(WebAttributes.ACCESS_DENIED_403, accessDeniedException);

		if (accessDeniedException instanceof MissingCsrfTokenException) {
			// セッションタイムアウトと判断
			logger.debug("Responding with 401 status code");
			response.sendError(HttpStatus.UNAUTHORIZED.value());
			return;
		}
		logger.debug("Responding with 403 status code");
		response.sendError(HttpStatus.FORBIDDEN.value());
	}

}
