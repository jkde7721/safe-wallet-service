package com.wanted.safewallet.global.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    ALREADY_EXISTS_BUDGET(BAD_REQUEST, "해당 월, 해당 카테고리의 예산 설정이 이미 존재합니다."),
    NOT_FOUND_CATEGORY(NOT_FOUND, "존재하지 않는 카테고리입니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
