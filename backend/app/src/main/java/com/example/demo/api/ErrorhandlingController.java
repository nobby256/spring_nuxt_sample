package com.example.demo.api;

import java.io.Serializable;
import java.util.Objects;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import org.jspecify.annotations.Nullable;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.exception.DomainException;
import com.example.demo.exception.DomainProblem;
import com.example.demo.exception.ProblemMessage;

/**
 * SPAのサンプルプログラム（エラーハンドリング）から呼び出されるRESTコントローラ。
 */
// CHECKSTYLE.OFF: MagicNumber
@RestController
@RequestMapping(path = "/api/errorhandling")
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
		Objects.requireNonNull(value);
		if (value.length() == 1) {
			// 業務エラー
			DomainProblem problem = new DomainProblem(new ProblemMessage(new DefaultMessageSourceResolvable("E001")));
			problem.addMessage(new ProblemMessage(new DefaultMessageSourceResolvable("E002")));
			problem.addMessage(new ProblemMessage(new DefaultMessageSourceResolvable("E003")));
			DomainException exception = new DomainException(problem);
			throw exception;
		} else if (value.length() == 2) {
			// INTERNAL SERVER ERROR
			RuntimeException exception = new RuntimeException("システムエラーが発生しました");
			throw exception;
		} else if (value.length() == 3) {
			// UNAUTHORIZED
			// セッションタイムアウトを仮想で実現する為にセッションを意図的に破棄する
			request.getSession().invalidate();
			ResponseStatusException exception = new ResponseStatusException(HttpStatus.UNAUTHORIZED);
			throw exception;
		} else if (value.length() == 4) {
			// FORBIDDEN
			ResponseStatusException exception = new ResponseStatusException(HttpStatus.FORBIDDEN);
			throw exception;
		} else if (value.length() == 5) {
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
		@Nullable
		private String value;

		/**
		 * リクエストの値を取得する。
		 *
		 * @return リクエストの値
		 */
		public @Nullable String getValue() {
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
	record ResponseData(String message) {}
}
