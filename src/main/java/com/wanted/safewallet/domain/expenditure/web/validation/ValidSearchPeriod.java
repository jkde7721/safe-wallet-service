package com.wanted.safewallet.domain.expenditure.web.validation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Constraint(validatedBy = SearchPeriodValidator.class)
@Target(TYPE)
@Retention(RUNTIME)
public @interface ValidSearchPeriod {

    String message() default "조회 가능 기간은 최대 1년입니다.";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
