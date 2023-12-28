package com.wanted.safewallet.domain.expenditure.business.dto;

import java.time.LocalDate;
import java.time.YearMonth;
import lombok.Getter;

@Getter
public class TodayExpenditureConsultDateDto {

    private final LocalDate now;
    private final YearMonth budgetYearMonth;
    private final LocalDate expenditureStartDate;
    private final LocalDate expenditureEndDate;
    private final int daysOfCurrentMonth;
    private final int leftDaysOfCurrentMonth;

    public TodayExpenditureConsultDateDto(LocalDate now) {
        this.now = now;
        this.budgetYearMonth = YearMonth.of(now.getYear(), now.getMonth());
        this.expenditureStartDate = now.withDayOfMonth(1);
        this.expenditureEndDate = now.minusDays(1); //어제까지의 지출
        this.daysOfCurrentMonth = now.lengthOfMonth();
        this.leftDaysOfCurrentMonth = daysOfCurrentMonth - now.getDayOfMonth() + 1;
    }
}
