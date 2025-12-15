package com.example.demo.library.errors;

public class DomainProblemMessage extends DomainProblem<DomainProblemMessage> {

    public DomainProblemMessage() {
        super(DomainProblem.TYPE + "/message");
    }

}
