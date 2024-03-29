package com.maveric.digital.exceptions;

public class ProjectAlreadyExistsException extends AbstractNotFoundException {
    public ProjectAlreadyExistsException(String s) {
        super(s);
    }
}
