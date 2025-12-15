package com.example.demo.library.errors;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jspecify.annotations.Nullable;

public class DefaultDomainProblem implements DomainProblem<DomainError> {

    public static final URI TYPE = URI.create("/domain-problem");

    private @Nullable String detail;

    private List<DomainError> errors = new ArrayList<>();

    @Override
    public URI getType() {
        return TYPE;
    }

    @Override
    public @Nullable String getDetail() {
        return detail;
    }

    @Override
    public void setDetail(@Nullable String detail) {
        this.detail = detail;
    }

    public void addError(DomainError error) {
        errors.add(error);
    }

    @Override
    public List<DomainError> getErrors() {
        return Collections.unmodifiableList(errors);
    }

}
