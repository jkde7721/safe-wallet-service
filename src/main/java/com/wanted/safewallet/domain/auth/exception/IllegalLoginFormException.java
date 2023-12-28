package com.wanted.safewallet.domain.auth.exception;

import org.springframework.security.core.AuthenticationException;

public class IllegalLoginFormException extends AuthenticationException {

    public IllegalLoginFormException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public IllegalLoginFormException(String msg) {
        super(msg);
    }
}
