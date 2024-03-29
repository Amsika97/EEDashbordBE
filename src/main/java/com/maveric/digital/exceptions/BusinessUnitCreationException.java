package com.maveric.digital.exceptions;

public class BusinessUnitCreationException extends AbstractNotFoundException {
    public BusinessUnitCreationException(String message) {
        super(message);
    }
}