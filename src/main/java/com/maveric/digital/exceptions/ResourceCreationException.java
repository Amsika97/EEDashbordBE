package com.maveric.digital.exceptions;

public class ResourceCreationException extends RuntimeException{
    public ResourceCreationException(String message) {
        super(message);
    }

    public ResourceCreationException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
