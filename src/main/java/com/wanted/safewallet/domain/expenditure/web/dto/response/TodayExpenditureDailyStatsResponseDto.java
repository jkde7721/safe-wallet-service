package com.wanted.safewallet.domain.expenditure.web.dto.response;

import com.wanted.safewallet.domain.category.persistence.entity.CategoryType;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TodayExpenditureDailyStatsResponseDto {

    private Long todayTotalAmount;

    private List<TodayExpenditureDailyStatsOfCategoryResponseDto> todayExpenditureDailyStatsOfCategoryList;

    @Getter
    @AllArgsConstructor
    public static class TodayExpenditureDailyStatsOfCategoryResponseDto {

        private Long categoryId;

        private CategoryType type;

        private Long consultedTotalAmount;

        private Long todayTotalAmount;

        private Long consumptionRate;
    }
}
