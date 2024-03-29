package com.maveric.digital.exceptions;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus
public class UserNotFoundException extends AbstractNotFoundException {
    public UserNotFoundException(String message) {
        super(message);
    }

}
