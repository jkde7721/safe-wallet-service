package com.wanted.safewallet.domain.auth.exception;

import org.springframework.security.core.AuthenticationException;

public class DirtyTokenException extends AuthenticationException {

    public DirtyTokenException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public DirtyTokenException(String msg) {
        super(msg);
    }
}
