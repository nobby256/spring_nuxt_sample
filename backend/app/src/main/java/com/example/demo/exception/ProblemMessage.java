package com.example.demo.exception;

import java.io.Serializable;

import org.jspecify.annotations.Nullable;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.DefaultMessageSourceResolvable;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

public class ProblemMessage implements Serializable {

	@JsonIgnore
	protected final DefaultMessageSourceResolvable resolvable;

	public ProblemMessage(DefaultMessageSourceResolvable resolvable) {
		this.resolvable = resolvable;
	}

	public @Nullable String getCode() {
		return resolvable.getCode();
	}

	@Schema(type = "string", requiredMode = RequiredMode.REQUIRED)
	public MessageSourceResolvable getText() {
		return resolvable;
	}

}
