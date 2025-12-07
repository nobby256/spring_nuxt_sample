package com.example.demo.api;

import java.util.List;
import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 業務エラーをキャッチしREST呼び出しのレスポンスに変換するControllerAdvive。
 * <p>
 * {@link ControllerAdvice#basePackageClasses()}によって、このパッケージに含まれるControllerのみが対象となっています。
 * </p>
 */
@RestControllerAdvice(basePackageClasses = BusinessExceptionControllerAdvice.class)
public class BusinessExceptionControllerAdvice {

    /** {@link MessageSource}。 */
    private final MessageSource messageSource;

    /**
     * コンストラクタ。
     * 
     * @param messageSource {@link MessageSource}
     */
    public BusinessExceptionControllerAdvice(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    /**
     * 業務例外をREST呼び出しのレスポンス/に変換する。
     * 
     * @param exception {@link BusinessException}
     * @return {@link ResponseEntity}
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<List<ErrorMessage>> handle(BusinessException exception) {
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage(exception, locale);

        // このサンプルのクライアントは業務エラーをalert()で表示するだけなのでシンブルに設計しています。
        // クライアントがよりリッチな表現でメッセージを表示するのであれば、
        // メッセージに対して様々な情報を持たせる構造に変更してください。
        ErrorMessage ErrorMessage = new ErrorMessage(exception.getCode(), message);

        // 業務例外のステータスコードはUNPROCESSABLE_CONTENT(422)を使用します。
        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_CONTENT)
                .contentType(MediaType.APPLICATION_JSON)
                .body(List.of(ErrorMessage));
    }

    /**
     * 業務エラーメッセージ。
     * 
     * @param code    エラーコード
     * @param message エラーメッセージ
     */
    record ErrorMessage(String code, String message) {
    }

}
