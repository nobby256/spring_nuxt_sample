package com.example.demo.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.demo.exception.DomainException;
import com.example.demo.exception.DomainProblem;

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
    @ExceptionHandler(exception = DomainException.class)
    @ApiResponse(responseCode = "422", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = DomainProblem.class)))
    public ResponseEntity<DomainProblem> handleDomainProblem(DomainException exception) {
        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_CONTENT)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(exception.getProblem());
    }

}
