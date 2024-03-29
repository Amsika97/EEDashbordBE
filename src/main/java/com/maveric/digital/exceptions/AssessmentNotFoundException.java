package com.maveric.digital.exceptions;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus
public class AssessmentNotFoundException extends AbstractNotFoundException {

	private static final long serialVersionUID = -3941330046783089629L;

	public AssessmentNotFoundException(String message) {
		super(message);
	}

}
