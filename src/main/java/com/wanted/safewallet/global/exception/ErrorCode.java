package com.wanted.safewallet.global.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    ALREADY_EXISTS_BUDGET(BAD_REQUEST, "해당 월, 해당 카테고리의 예산 설정이 이미 존재합니다."),
    NOT_FOUND_CATEGORY(NOT_FOUND, "존재하지 않는 카테고리입니다."),
    NOT_FOUND_EXPENDITURE(NOT_FOUND, "해당 지출 내역을 찾을 수 없습니다."),
    FORBIDDEN_EXPENDITURE(FORBIDDEN, "해당 지출 내역에 접근할 수 있는 권한이 없습니다."),
    ALREADY_EXISTS_USERNAME(BAD_REQUEST, "해당 계정명이 이미 존재합니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
