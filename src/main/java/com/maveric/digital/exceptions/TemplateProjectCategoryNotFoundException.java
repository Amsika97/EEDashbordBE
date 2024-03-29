package com.maveric.digital.exceptions;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus
public class TemplateProjectCategoryNotFoundException extends AbstractNotFoundException {

	private static final long serialVersionUID = -5810431009965799420L;

	public TemplateProjectCategoryNotFoundException(String message) {
		super(message);
	}

}
