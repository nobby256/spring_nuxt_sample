package com.example.demo.exception;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.example.demo.library.errors.AbstractDomainProblem;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

public class DomainProblem extends AbstractDomainProblem {

	private List<ProblemMessage> messages = new ArrayList<>();

	public DomainProblem(ProblemMessage error) {
		addMessage(error);
	}

	public void addMessage(ProblemMessage error) {
		messages.add(error);
	}

	@Schema(requiredMode = RequiredMode.REQUIRED)
	public List<ProblemMessage> getMessages() {
		return Collections.unmodifiableList(messages);
	}

}
