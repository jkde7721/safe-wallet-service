package com.wanted.safewallet.global.exception;

import static java.util.stream.Collectors.joining;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

import com.wanted.safewallet.global.dto.response.CommonResponse;
import java.util.stream.Stream;
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
        String errorMessage = getErrorMessage(e);
        return ResponseEntity.status(BAD_REQUEST)
            .body(new CommonResponse<>(BAD_REQUEST.value(), BAD_REQUEST.name(),
                errorMessage, null));
    }

    private String getErrorMessage(MethodArgumentNotValidException e) {
        return Stream.concat(getObjectErrorMessages(e), getFieldErrorMessages(e))
            .collect(joining(", "));
    }

    private Stream<String> getObjectErrorMessages(MethodArgumentNotValidException e) {
        return e.getBindingResult().getGlobalErrors().stream()
            .map(err -> String.format("[%s]", err.getDefaultMessage()));
    }

    private Stream<String> getFieldErrorMessages(MethodArgumentNotValidException e) {
        return e.getFieldErrors().stream()
            .map(err -> String.format("[%s: %s]", err.getField(), err.getDefaultMessage()));
    }
}
