package com.wanted.safewallet.domain.expenditure.web.dto.response;

import com.wanted.safewallet.domain.category.persistence.entity.CategoryType;
import com.wanted.safewallet.domain.expenditure.web.enums.FinanceStatus;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TodayExpenditureConsultResponseDto {

    private Long todayTotalAmount;

    private FinanceStatus totalFinanceStatus;

    private List<TodayExpenditureConsultOfCategoryResponseDto> todayExpenditureConsultOfCategoryList;

    @Getter
    @AllArgsConstructor
    public static class TodayExpenditureConsultOfCategoryResponseDto {

        private Long categoryId;

        private CategoryType type;

        private Long todayTotalAmount;

        private FinanceStatus financeStatus;
    }
}
