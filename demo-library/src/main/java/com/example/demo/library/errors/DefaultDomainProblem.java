package com.example.demo.library.errors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DefaultDomainProblem implements DomainProblem<DomainError> {

    private List<DomainError> errors = new ArrayList<>();

    public void addError(DomainError error) {
        errors.add(error);
    }

    @Override
    public List<DomainError> getErrors() {
        return Collections.unmodifiableList(errors);
    }

}
