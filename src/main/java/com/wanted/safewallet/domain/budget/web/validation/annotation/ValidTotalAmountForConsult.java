package com.wanted.safewallet.domain.budget.web.validation.annotation;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Min(value = 0, message = "{budget.consult.min}")
@Max(value = 100_000_000, message = "{budget.consult.max}")
@NotNull(message = "{budget.consult.notNull}")
@Constraint(validatedBy = { })
@Target(PARAMETER)
@Retention(RUNTIME)
public @interface ValidTotalAmountForConsult {

    String message() default "";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
