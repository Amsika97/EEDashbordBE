package com.maveric.digital.exceptions;

public class AccountsNotFoundException extends RuntimeException {
    public AccountsNotFoundException(String message) {
        super(message);
    }

}
