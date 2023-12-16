package com.wanted.safewallet.domain.budget.persistence.repository;

import com.wanted.safewallet.domain.budget.persistence.dto.BudgetAmountOfCategoryListDto;
import java.time.YearMonth;
import java.util.List;

public interface BudgetRepositoryCustom {

    boolean existsByUserAndBudgetYearMonthAndCategories(String userId, YearMonth budgetYearMonth,
        List<Long> categoryIds);

    BudgetAmountOfCategoryListDto findBudgetAmountOfCategoryListByUserAndBudgetYearMonth(String userId, YearMonth budgetYearMonth);
}
