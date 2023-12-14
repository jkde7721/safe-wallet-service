package com.wanted.safewallet.domain.budget.web.dto.response;

import com.wanted.safewallet.domain.category.persistence.entity.CategoryType;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BudgetSetUpResponseDto {

    private List<BudgetOfCategory> budgetList;

    @Getter
    @AllArgsConstructor
    public static class BudgetOfCategory {

        private Long budgetId;
        private Long categoryId;
        private CategoryType type;
        private Long amount;
    }
}
