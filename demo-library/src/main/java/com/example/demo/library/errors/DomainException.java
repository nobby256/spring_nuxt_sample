package com.example.demo.library.errors;

import java.util.Arrays;

import org.springframework.util.Assert;

public class DomainException extends RuntimeException {

    private final DomainProblem problem;

    public DomainException(DomainProblem problem) {
        Assert.notNull(problem, "problem must not be null.");
        this.problem = problem;
    }

    public DomainProblem getProblem() {
        return problem;
    }

    @Override
    public String getMessage() {
        String detail = problem.getDetail();
        if (detail != null && !detail.isEmpty()) {
            return detail;
        }
        String code = problem.getCode();
        Object[] args = problem.getArguments();
        if (code != null) {
            return "code=" + code + (args != null && args.length > 0
                    ? ", args=" + Arrays.toString(args)
                    : "");
        }
        // code も detail も無い場合の最後の保険
        return super.getMessage();
    }

}
