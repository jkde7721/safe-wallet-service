package com.wanted.safewallet.domain.expenditure.web.dto.response;

import com.wanted.safewallet.domain.category.persistence.entity.CategoryType;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ExpenditureStatsResponse {

    private LocalDate currentStartDate;

    private LocalDate currentEndDate;

    private LocalDate criteriaStartDate;

    private LocalDate criteriaEndDate;

    private Long totalConsumptionRate;

    private List<ConsumptionRateOfCategoryResponse> consumptionRateOfCategoryList;

    @Getter
    @AllArgsConstructor
    public static class ConsumptionRateOfCategoryResponse {

        private Long categoryId;

        private CategoryType type;

        private Long consumptionRate;
    }
}
