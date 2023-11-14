package com.wanted.safewallet.domain.budget.persistence.repository;

import java.time.YearMonth;
import java.util.List;

public interface BudgetRepositoryCustom {

    boolean existsByUserIdAndBudgetYearMonthAndInCategories(String userId,
        YearMonth budgetYearMonth, List<Long> categoryIds);
}
