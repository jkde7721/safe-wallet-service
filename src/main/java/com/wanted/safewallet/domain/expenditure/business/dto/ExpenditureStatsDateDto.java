package com.wanted.safewallet.domain.expenditure.business.dto;

import static com.wanted.safewallet.domain.expenditure.web.enums.StatsCriteria.LAST_MONTH;
import static com.wanted.safewallet.domain.expenditure.web.enums.StatsCriteria.LAST_YEAR;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.TemporalAdjusters.previousOrSame;

import com.wanted.safewallet.domain.expenditure.web.enums.StatsCriteria;
import java.time.DayOfWeek;
import java.time.LocalDate;
import lombok.Getter;

@Getter
public class ExpenditureStatsDateDto {

    private final LocalDate currentStartDate;
    private final LocalDate currentEndDate;
    private final LocalDate criteriaStartDate;
    private final LocalDate criteriaEndDate;

    public ExpenditureStatsDateDto(LocalDate now, StatsCriteria criteria) {
        this.currentStartDate = getCurrentStartDate(now, criteria);
        this.currentEndDate = now;
        this.criteriaStartDate = getCriteriaStartDate(currentStartDate, criteria);
        this.criteriaEndDate = getCriteriaEndDate(criteriaStartDate, DAYS.between(currentStartDate, currentEndDate));
    }

    private LocalDate getCurrentStartDate(LocalDate currentEndDate, StatsCriteria criteria) {
        if (criteria == LAST_YEAR) {
            return LocalDate.of(currentEndDate.getYear(), 1, 1);
        }
        else if (criteria == LAST_MONTH) {
            return LocalDate.of(currentEndDate.getYear(), currentEndDate.getMonth(), 1);
        }
        else {
            return currentEndDate.with(previousOrSame(DayOfWeek.MONDAY));
        }
    }

    private LocalDate getCriteriaStartDate(LocalDate currentStartDate, StatsCriteria criteria) {
        if (criteria == LAST_YEAR) {
            return currentStartDate.minusYears(1);
        }
        else if (criteria == LAST_MONTH) {
            return currentStartDate.minusMonths(1);
        }
        else {
            return currentStartDate.minusWeeks(1);
        }
    }

    private LocalDate getCriteriaEndDate(LocalDate criteriaStartDate, long durationOfDays) {
        return criteriaStartDate.plusDays(durationOfDays);
    }
}
