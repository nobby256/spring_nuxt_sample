package com.example.demo.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.demo.library.errors.DomainProblem;
import com.example.demo.library.errors.DomainProblemException;
import com.example.demo.library.errors.DomainProblemMessage;

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
     * @param exception {@link DomainProblemException}
     * @return {@link ResponseEntity}
     */
    @ExceptionHandler(exception = DomainProblemException.class)
    @ApiResponse(responseCode = "422", content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = DomainProblemMessage.class)))
    public ResponseEntity<DomainProblemMessage> handle(DomainProblemException exception) {
        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_CONTENT)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(getDomainProblemMessage(exception));
    }

    DomainProblemMessage getDomainProblemMessage(DomainProblemException exception) {
        DomainProblem problem = exception.getProblem();
        if (problem instanceof DomainProblemMessage dpm) {
            return dpm;
        }
        return new DomainProblemMessage(problem);
    }
}
