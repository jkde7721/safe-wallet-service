package com.wanted.safewallet.domain.user.web.validation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Constraint(validatedBy = EmailConstraintValidator.class)
@Target({FIELD, PARAMETER})
@Retention(RUNTIME)
public @interface ValidEmail {

    String message() default "유효하지 않은 이메일입니다.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
