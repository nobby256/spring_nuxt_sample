package com.example.demo.library.errors;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jspecify.annotations.Nullable;

public abstract class AbstractDomainProblem<T> implements DomainProblem {

    private @Nullable String detail;

    private List<T> errors = new ArrayList<>();

    @Override
    public URI getType() {
        return TYPE_BASE.resolve("/message");
    }

    @Override
    public @Nullable String getDetail() {
        return detail;
    }

    @Override
    public void setDetail(@Nullable String detail) {
        this.detail = detail;
    }

    public void addError(T error) {
        errors.add(error);
    }

    public List<T> getErrors() {
        return Collections.unmodifiableList(errors);
    }

}
