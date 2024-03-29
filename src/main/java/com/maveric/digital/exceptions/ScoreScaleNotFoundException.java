package com.maveric.digital.exceptions;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus
public class ScoreScaleNotFoundException extends AbstractNotFoundException {

	private static final long serialVersionUID = 4125963634542048695L;

	public ScoreScaleNotFoundException(String message) {
		super(message);
	}

	public ScoreScaleNotFoundException(String message, Throwable throwable) {
		super(message, throwable);
	}

}
