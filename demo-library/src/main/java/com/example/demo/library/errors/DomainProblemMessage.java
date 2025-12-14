package com.example.demo.library.errors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.context.support.DefaultMessageSourceResolvable;

public class DomainProblemMessage extends DomainProblem {

    private List<ProblemMessage> messages = new ArrayList<>();

    public DomainProblemMessage(DefaultMessageSourceResolvable resolvable) {
        super(resolvable);
    }

    public DomainProblemMessage(String detail) {
        super(detail);
    }

    public DomainProblemMessage(DomainProblem problem) {
        super(problem.getDetail());
    }

    public void addMessage(String text) {
        messages.add(new ProblemMessage(text));
    }

    public void addMessage(DefaultMessageSourceResolvable resolvable) {
        messages.add(new ProblemMessage(resolvable));
    }

    public List<ProblemMessage> getMessages() {
        return Collections.unmodifiableList(messages);
    }

}
