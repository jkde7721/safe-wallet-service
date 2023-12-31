package com.wanted.safewallet.global.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

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
    NOT_FOUND_BUDGET(NOT_FOUND, "해당 예산 내역을 찾을 수 없습니다."),
    FORBIDDEN_BUDGET(FORBIDDEN, "해당 예산 내역에 접근할 수 있는 권한이 없습니다."),
    ALREADY_EXISTS_USERNAME(BAD_REQUEST, "해당 계정명이 이미 존재합니다."),
    NOT_FOUND_USER(NOT_FOUND, "해당 사용자를 찾을 수 없습니다."),
    UNAUTHORIZED_JWT_TOKEN(UNAUTHORIZED, "유효하지 않은 JWT 토큰입니다."),
    PASSWORD_ENCODING_ERROR(INTERNAL_SERVER_ERROR, "사용자의 비밀번호가 암호화되지 않았습니다."),
    ALREADY_AUTHENTICATED_MAIL(BAD_REQUEST, "이미 인증된 메일입니다."),
    EXPIRED_MAIL_AUTH(BAD_REQUEST, "만료된 메일 인증입니다."),
    MAIL_SEND_ERROR(INTERNAL_SERVER_ERROR, "메일 전송에 실패하였습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
