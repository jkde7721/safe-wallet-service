package com.wanted.safewallet.domain.expenditure.web.dto.response;

import com.wanted.safewallet.domain.category.persistence.entity.CategoryType;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ExpenditureStatsResponseDto {

    private LocalDate currentStartDate;

    private LocalDate currentEndDate;

    private LocalDate criteriaStartDate;

    private LocalDate criteriaEndDate;

    private Long totalConsumptionRate;

    private List<ConsumptionRateByCategoryResponseDto> consumptionRateListByCategory;

    @Getter
    @AllArgsConstructor
    public static class ConsumptionRateByCategoryResponseDto {

        private Long categoryId;

        private CategoryType type;

        private Long consumptionRate;
    }
}
