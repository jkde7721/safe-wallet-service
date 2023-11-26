package com.wanted.safewallet.domain.expenditure.web.dto.request;

import com.wanted.safewallet.domain.expenditure.web.validation.ValidSearchPeriod;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@ValidSearchPeriod
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExpenditureSearchCond {

    private LocalDate startDate = LocalDate.now().minusMonths(1);

    private LocalDate endDate = LocalDate.now();

    private List<Long> categories = List.of();

    @Min(value = 0, message = "{expenditure.search.amount.min}")
    private Long minAmount = 0L;

    @Max(value = 100_000_000, message = "{expenditure.search.amount.max}")
    private Long maxAmount = 1000_000L;

    private List<Long> excepts = List.of(); //지출 id 리스트
}
