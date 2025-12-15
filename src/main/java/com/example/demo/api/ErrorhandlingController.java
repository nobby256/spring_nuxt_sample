package com.example.demo.api;

import java.io.Serializable;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.library.errors.DefaultDomainProblem;
import com.example.demo.library.errors.DomainError;
import com.example.demo.library.errors.DomainProblemException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

/**
 * SPAのサンプルプログラム（エラーハンドリング）から呼び出されるRESTコントローラ。
 */
// CHECKSTYLE.OFF: MagicNumber
@RestController
@RequestMapping(path = "/api/errorhandling", produces = MediaType.APPLICATION_JSON_VALUE)
public class ErrorhandlingController {

	/**
	 * リクエストを受け取り、エラーを発生させる。
	 *
	 * @param requestData リクエストボディ
	 * @param request     {@link HttpServletRequest}
	 * @return レスポンスボディ
	 */
	@PostMapping
	public ResponseData send(@RequestBody @Valid RequestData requestData, HttpServletRequest request) {
		String value = requestData.getValue();
		if (value.length() == 1) {
			// 業務エラー（エラーメッセージなし）
			DefaultDomainProblem problem = new DefaultDomainProblem();
			DomainProblemException exception = new DomainProblemException(problem);
			throw exception;
		} else if (value.length() == 2) {
			// 業務エラー（エラーメッセージあり）
			DefaultDomainProblem problem = new DefaultDomainProblem();
			problem.addError(new DomainError(new DefaultMessageSourceResolvable("E001")));
			problem.addError(new DomainError(new DefaultMessageSourceResolvable("E002")));
			problem.addError(new DomainError(new DefaultMessageSourceResolvable("E003")));
			DomainProblemException exception = new DomainProblemException(problem);
			throw exception;
		} else if (value.length() == 3) {
			// INTERNAL SERVER ERROR
			RuntimeException exception = new RuntimeException("システムエラーが発生しました");
			throw exception;
		} else if (value.length() == 4) {
			// UNAUTHORIZED
			// セッションタイムアウトを仮想で実現する為にセッションを意図的に破棄する
			request.getSession().invalidate();
			ResponseStatusException exception = new ResponseStatusException(HttpStatus.UNAUTHORIZED);
			throw exception;
		} else if (value.length() == 5) {
			// FORBIDDEN
			ResponseStatusException exception = new ResponseStatusException(HttpStatus.FORBIDDEN);
			throw exception;
		} else if (value.length() == 6) {
			// NOT FOUND
			ResponseStatusException exception = new ResponseStatusException(HttpStatus.NOT_FOUND);
			throw exception;
		}
		return new ResponseData("OK");
	}

	/**
	 * リクエストデータ。
	 */
	public static class RequestData implements Serializable {
		/** リクエストの値。 */
		@NotBlank
		private String value;

		/**
		 * リクエストの値を取得する。
		 *
		 * @return リクエストの値
		 */
		public String getValue() {
			return value;
		}

		/**
		 * リクエストの値を設定する。
		 *
		 * @param value リクエストの値
		 */
		public void setValue(String value) {
			this.value = value;
		}

	}

	/**
	 * レスポンスデータ。
	 * 
	 * @param message メッセージ
	 */
	record ResponseData(String message) {

	}
}
