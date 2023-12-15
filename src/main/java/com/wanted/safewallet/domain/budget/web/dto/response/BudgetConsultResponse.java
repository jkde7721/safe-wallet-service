package com.wanted.safewallet.domain.budget.web.dto.response;

import com.wanted.safewallet.domain.category.persistence.entity.CategoryType;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BudgetConsultResponse {

    private List<BudgetConsultOfCategoryResponse> budgetConsultList;

    @Getter
    @AllArgsConstructor
    public static class BudgetConsultOfCategoryResponse {

        private Long categoryId;

        private CategoryType type;

        private Long amount;
    }
}
