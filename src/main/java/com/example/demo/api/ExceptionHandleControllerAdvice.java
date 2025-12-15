package com.example.demo.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.demo.library.errors.DefaultDomainProblem;
import com.example.demo.library.errors.DomainException;
import com.example.demo.library.errors.DomainProblem;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

/**
 * 業務エラーをキャッチしREST呼び出しのレスポンスに変換するControllerAdvive。
 * <p>
 * {@link RestControllerAdvice#basePackageClasses()}によって、このパッケージに含まれるControllerのみが対象となっています。
 * </p>
 */
@RestControllerAdvice(basePackageClasses = ExceptionHandleControllerAdvice.class)
public class ExceptionHandleControllerAdvice {

    /**
     * 業務例外をRFC7807準拠のレスポンスに変換する。
     * 
     * @param exception {@link DomainException}
     * @return {@link DomainProblem}
     */
    @ExceptionHandler(exception = DomainException.class, produces = MediaType.APPLICATION_PROBLEM_JSON_VALUE)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_CONTENT)
    // returnするDomainProblemが複数種類ある場合は、すべてのクラスをoneOfに列挙する事
    // @formatter:off
    @ApiResponse(responseCode = "422", content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE, 
        schema = @Schema(oneOf = DefaultDomainProblem.class)))
    // @formatter:on
    public DomainProblem<?> handleDomainProblem(DomainException exception) {
        return exception.getProblem();
    }

}
