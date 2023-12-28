package com.wanted.safewallet.domain.budget.business.dto;

import com.wanted.safewallet.domain.category.persistence.entity.CategoryType;
import java.time.YearMonth;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class BudgetUpdateDto {

    private YearMonth budgetYearMonth;

    private Long categoryId;

    private CategoryType type;

    private Long amount;
}
