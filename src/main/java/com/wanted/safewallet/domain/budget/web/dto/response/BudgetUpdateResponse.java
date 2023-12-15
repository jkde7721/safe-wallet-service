package com.wanted.safewallet.domain.budget.web.dto.response;

import com.wanted.safewallet.domain.category.persistence.entity.CategoryType;
import java.time.YearMonth;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class BudgetUpdateResponse {

    private Long budgetId;

    private YearMonth budgetYearMonth;

    private Long categoryId;

    private CategoryType type;

    private Long amount;
}
