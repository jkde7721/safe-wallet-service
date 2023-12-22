package com.wanted.safewallet.domain.expenditure.business.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;

@Getter
public class ExpenditureSearchCond {

    private final LocalDateTime startDate;

    private final LocalDateTime endDate;

    private final List<Long> categories;

    private final Long minAmount;

    private final Long maxAmount;

    private final List<Long> excepts;

    public ExpenditureSearchCond(LocalDate startDate, LocalDate endDate, List<Long> categories,
        Long minAmount, Long maxAmount, List<Long> excepts) {
        this.startDate = startDate.atStartOfDay();
        this.endDate = endDate.plusDays(1).atStartOfDay();
        this.categories = categories;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
        this.excepts = excepts;
    }
}
