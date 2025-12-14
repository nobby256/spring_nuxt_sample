package com.example.demo.library.errors;

import org.springframework.util.Assert;

public class DomainProblemException extends RuntimeException {

    private final DomainProblem problem;

    public DomainProblemException(DomainProblem problem) {
        this(null, problem);
    }

    public DomainProblemException(Throwable cause, DomainProblem problem) {
        super(cause);
        Assert.notNull(problem, "problem must not be null.");
        this.problem = problem;
    }

    public DomainProblem getProblem() {
        return problem;
    }

    @Override
    public String getMessage() {
        return problem.toString();
    }

}
