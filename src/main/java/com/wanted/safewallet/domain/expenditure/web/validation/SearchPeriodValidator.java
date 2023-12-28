package com.wanted.safewallet.domain.expenditure.web.validation;

import com.wanted.safewallet.domain.expenditure.web.dto.request.ExpenditureSearchRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class SearchPeriodValidator implements ConstraintValidator<ValidSearchPeriod, ExpenditureSearchRequest> {

    private static final long MAX_SEARCH_PERIOD_DAYS = 365;

    @Override
    public void initialize(ValidSearchPeriod constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(ExpenditureSearchRequest request, ConstraintValidatorContext context) {
        LocalDate startDate = request.getStartDate();
        LocalDate endDate = request.getEndDate();
        long periodDays = ChronoUnit.DAYS.between(startDate, endDate);
        return periodDays <= MAX_SEARCH_PERIOD_DAYS;
    }
}
