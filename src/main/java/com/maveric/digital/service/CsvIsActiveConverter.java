package com.maveric.digital.service;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvConstraintViolationException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;

public class CsvIsActiveConverter extends AbstractBeanField {
	@Override
	protected Object convert(String value) throws CsvDataTypeMismatchException, CsvConstraintViolationException {
		return value.equalsIgnoreCase("no") ? false : true;
	}

}
