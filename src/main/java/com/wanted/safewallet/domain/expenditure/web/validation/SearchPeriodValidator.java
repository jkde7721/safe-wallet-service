package com.wanted.safewallet.domain.expenditure.web.validation;

import com.wanted.safewallet.domain.expenditure.web.dto.request.ExpenditureSearchCond;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class SearchPeriodValidator implements ConstraintValidator<ValidSearchPeriod, ExpenditureSearchCond> {

    private static final long MAX_SEARCH_PERIOD_DAYS = 365;

    @Override
    public void initialize(ValidSearchPeriod constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(ExpenditureSearchCond searchCond, ConstraintValidatorContext context) {
        LocalDate startDate = searchCond.getStartDate();
        LocalDate endDate = searchCond.getEndDate();
        long periodDays = ChronoUnit.DAYS.between(startDate, endDate);
        return periodDays <= MAX_SEARCH_PERIOD_DAYS;
    }
}
