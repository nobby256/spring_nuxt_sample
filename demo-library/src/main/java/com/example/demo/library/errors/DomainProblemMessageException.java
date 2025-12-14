package com.example.demo.library.errors;

public class DomainProblemMessageException extends DomainProblemException {

    public DomainProblemMessageException(DomainProblemMessage problem) {
        this(null, problem);
    }

    public DomainProblemMessageException(Throwable cause, DomainProblemMessage problem) {
        super(cause, problem);
    }

    @Override
    public DomainProblemMessage getProblem() {
        return (DomainProblemMessage) super.getProblem();
    }

}
