package com.wanted.safewallet.domain.expenditure.web.dto.response;

import com.wanted.safewallet.domain.category.persistence.entity.CategoryType;
import com.wanted.safewallet.domain.expenditure.business.enums.FinanceStatus;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TodayExpenditureConsultResponse {

    private Long totalAmount;

    private FinanceStatus totalFinanceStatus;

    private List<TodayExpenditureConsultOfCategoryResponse> todayExpenditureConsultOfCategoryList;

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TodayExpenditureConsultOfCategoryResponse {

        private Long categoryId;

        private CategoryType type;

        private Long amount;

        private FinanceStatus financeStatus;
    }
}
