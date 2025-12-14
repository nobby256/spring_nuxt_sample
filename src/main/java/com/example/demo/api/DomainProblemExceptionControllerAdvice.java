package com.example.demo.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.demo.library.errors.DomainProblem;
import com.example.demo.library.errors.DomainProblemException;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

/**
 * 業務エラーをキャッチしREST呼び出しのレスポンスに変換するControllerAdvive。
 * <p>
 * {@link ControllerAdvice#basePackageClasses()}によって、このパッケージに含まれるControllerのみが対象となっています。
 * </p>
 */
@RestControllerAdvice(basePackageClasses = DomainProblemExceptionControllerAdvice.class)
public class DomainProblemExceptionControllerAdvice {

    /**
     * 業務例外をRFC7807準拠のレスポンスに変換する。
     * 
     * @param exception {@link DomainProblemException}
     * @return {@link ResponseEntity}
     */
    @ExceptionHandler(exception = DomainProblemException.class)
    @ApiResponse(responseCode = "422", content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = DomainProblem.class)))
    public ResponseEntity<DomainProblem> handle(DomainProblemException exception) {
        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_CONTENT)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(exception.getProblem());
    }

}
