package com.maveric.digital.exceptions;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus
public class TemplateNotFoundException extends AbstractNotFoundException {
	private static final long serialVersionUID = 2083739071949257497L;

	public TemplateNotFoundException(String message) {
		super(message);
	}

	public TemplateNotFoundException(String message, Throwable throwable) {
		super(message, throwable);
	}

}
