package com.maveric.digital.exceptions;

public class InvalidAccountException extends AbstractNotFoundException {
    public InvalidAccountException(String s) {
        super(s);
    }
}