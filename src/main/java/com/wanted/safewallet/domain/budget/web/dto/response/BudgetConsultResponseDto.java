package com.wanted.safewallet.domain.budget.web.dto.response;

import com.wanted.safewallet.domain.category.persistence.entity.CategoryType;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BudgetConsultResponseDto {

    private List<BudgetConsultOfCategoryResponseDto> budgetConsultList;

    @Getter
    @AllArgsConstructor
    public static class BudgetConsultOfCategoryResponseDto {

        private Long categoryId;

        private CategoryType type;

        private Long amount;
    }
}
