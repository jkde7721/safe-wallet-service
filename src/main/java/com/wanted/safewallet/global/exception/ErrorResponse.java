package com.wanted.safewallet.global.exception;

import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class ErrorResponse {

    private final LocalDateTime timestamp;
    private final String status;
    private final String code;
    private final String message;

    public ErrorResponse(ErrorCode errorCode) {
        timestamp = LocalDateTime.now();
        status = errorCode.getHttpStatus().name();
        code = errorCode.name();
        message = errorCode.getMessage();
    }
}
