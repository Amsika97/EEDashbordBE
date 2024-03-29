package com.maveric.digital.exceptions;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus
public class ScoreCategoryNotFoundException extends AbstractNotFoundException {
	private static final long serialVersionUID = 8764922496877860475L;

	public ScoreCategoryNotFoundException(String message) {
		super(message);
	}

	public ScoreCategoryNotFoundException(String message, Throwable throwable) {
		super(message, throwable);
	}

}
