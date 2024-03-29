package com.maveric.digital.exceptions;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus
public class AssessmentProjectCategoryNotFoundException extends AbstractNotFoundException {

	private static final long serialVersionUID = -603641355132727383L;

	public AssessmentProjectCategoryNotFoundException(String message) {
		super(message);
	}

}
