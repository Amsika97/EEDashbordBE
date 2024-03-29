package com.maveric.digital.exceptions;

public abstract class AbstractNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 1796413372243621730L;

	protected AbstractNotFoundException(String message) {
		super(message);
	}

	protected AbstractNotFoundException(String message, Throwable throwable) {
		super(message, throwable);
	}

}
