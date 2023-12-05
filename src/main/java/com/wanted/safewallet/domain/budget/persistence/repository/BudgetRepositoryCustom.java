package com.wanted.safewallet.domain.budget.persistence.repository;

import com.wanted.safewallet.domain.budget.persistence.dto.response.TotalAmountByCategoryResponseDto;
import java.time.YearMonth;
import java.util.List;

public interface BudgetRepositoryCustom {

    boolean existsByUserIdAndBudgetYearMonthAndInCategories(String userId,
        YearMonth budgetYearMonth, List<Long> categoryIds);

    List<TotalAmountByCategoryResponseDto> getTotalAmountByCategoryList(String userId);

    List<TotalAmountByCategoryResponseDto> getTotalAmountByCategoryList();
}
