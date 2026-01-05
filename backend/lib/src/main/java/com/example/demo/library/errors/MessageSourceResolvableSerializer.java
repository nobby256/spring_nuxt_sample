package com.example.demo.library.errors;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.i18n.LocaleContextHolder;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ser.std.StdSerializer;

public class MessageSourceResolvableSerializer extends StdSerializer<MessageSourceResolvable> {

	private final MessageSource messageSource;

	public MessageSourceResolvableSerializer(MessageSource messageSource) {
		super(MessageSourceResolvable.class);
		this.messageSource = messageSource;
	}

	@Override
	public void serialize(MessageSourceResolvable value, JsonGenerator gen, SerializationContext ctxt)
			throws JacksonException {
		if (value == null) {
			gen.writeNull();
			return;
		}

		Locale locale = LocaleContextHolder.getLocale();
		String resolved = messageSource.getMessage(value, locale);

		gen.writeString(resolved);
	}
}
