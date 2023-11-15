package com.wanted.safewallet.global.dto.response.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.http.HttpStatus;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CommonResponseContent {

    HttpStatus status() default HttpStatus.OK;

    String message() default "요청이 정상 처리되었습니다.";
}
