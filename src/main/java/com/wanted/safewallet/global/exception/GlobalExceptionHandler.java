package com.wanted.safewallet.global.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import com.wanted.safewallet.global.dto.response.CommonResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<CommonResponse<Void>> handleBusinessException(BusinessException e) {
        ErrorCode errorCode = e.getErrorCode();
        return ResponseEntity.status(errorCode.getHttpStatus())
            .body(new CommonResponse<>(errorCode.getHttpStatus().value(), errorCode.name(),
                errorCode.getMessage(), null));
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
        MethodArgumentNotValidException e, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        StringBuilder sb = new StringBuilder();
        e.getBindingResult().getFieldErrors().forEach(f ->
            sb.append("[").append(f.getField()).append(": ").append(f.getDefaultMessage()).append("] "));
        return ResponseEntity.status(BAD_REQUEST)
            .body(new CommonResponse<>(BAD_REQUEST.value(), BAD_REQUEST.name(),
                sb.toString(), null));
    }
}
