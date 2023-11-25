package com.wanted.safewallet.domain.expenditure.web.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExpenditureSearchCond {

    //TODO: 조회 날짜 기간은 최대 1년
    private LocalDate startDate = LocalDate.now().minusMonths(1);

    private LocalDate endDate = LocalDate.now();

    private List<Long> categories = List.of();

    @Min(0)
    private Long minAmount = 0L;

    @Max(100000000)
    private Long maxAmount = 1000000L;

    private List<Long> excepts = List.of(); //지출 id 리스트
}
