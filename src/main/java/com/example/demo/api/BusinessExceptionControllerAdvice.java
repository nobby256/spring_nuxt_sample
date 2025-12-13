package com.example.demo.api;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.example.demo.library.errors.DomainException;
import com.example.demo.library.errors.DomainProblem;

/**
 * 業務エラーをキャッチしREST呼び出しのレスポンスに変換するControllerAdvive。
 * <p>
 * {@link ControllerAdvice#basePackageClasses()}によって、このパッケージに含まれるControllerのみが対象となっています。
 * </p>
 */
@ControllerAdvice(basePackageClasses = BusinessExceptionControllerAdvice.class)
@RequestMapping(produces = MediaType.APPLICATION_PROBLEM_JSON_VALUE)
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
     * 業務例外をRFC7807準拠のレスポンスに変換する。
     * 
     * @param exception {@link DomainException}
     * @return {@link DomainProblem}
     */
    @ExceptionHandler(DomainException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_CONTENT)
    public DomainProblem handle(DomainException exception) {
        Locale locale = LocaleContextHolder.getLocale();
        DomainProblem problem = exception.getProblem();
        String message = messageSource.getMessage(problem, locale);
        if (!StringUtils.hasText(problem.getDetail())) {
            problem.setDetail(message);
        }
        return problem;
    }

}
