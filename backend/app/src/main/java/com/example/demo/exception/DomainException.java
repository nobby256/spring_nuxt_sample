package com.example.demo.exception;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.exception.ContextedRuntimeException;
import org.springframework.util.Assert;

public class DomainException extends ContextedRuntimeException {

    private final DomainProblem problem;

    public DomainException(DomainProblem problem) {
        this(null, problem);
    }

    public DomainException(Throwable cause, DomainProblem problem) {
        super(cause);
        Assert.notNull(problem, "problem must not be null.");
        this.problem = problem;
    }

    public DomainProblem getProblem() {
        return problem;
    }

    @Override
    public String getMessage() {
        return ToStringBuilder.reflectionToString(problem);
    }

}
