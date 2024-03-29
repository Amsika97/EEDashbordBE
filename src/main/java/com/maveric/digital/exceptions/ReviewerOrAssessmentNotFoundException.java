package com.maveric.digital.exceptions;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus
public class ReviewerOrAssessmentNotFoundException extends AbstractNotFoundException {

	private static final long serialVersionUID = 3929380716133614771L;

	public ReviewerOrAssessmentNotFoundException(String message) {
		super(message);
	}

}
