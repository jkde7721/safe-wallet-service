package com.wanted.safewallet.domain.expenditure.web.dto.response;

import com.wanted.safewallet.domain.category.persistence.entity.CategoryType;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class YesterdayExpenditureDailyStatsResponse {

    private Long totalAmount;

    private List<YesterdayExpenditureDailyStatsOfCategoryResponse> yesterdayExpenditureDailyStatsOfCategoryList;

    @Getter
    @AllArgsConstructor
    public static class YesterdayExpenditureDailyStatsOfCategoryResponse {

        private Long categoryId;

        private CategoryType type;

        private Long consultedAmount;

        private Long expendedAmount;

        private Long consumptionRate;
    }
}
