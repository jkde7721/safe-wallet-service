package com.wanted.safewallet.domain.budget.persistence.repository;

import com.wanted.safewallet.domain.budget.persistence.dto.response.TotalAmountByCategoryResponseDto;
import com.wanted.safewallet.domain.category.persistence.entity.Category;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

public interface BudgetRepositoryCustom {

    boolean existsByUserIdAndBudgetYearMonthAndInCategories(String userId,
        YearMonth budgetYearMonth, List<Long> categoryIds);

    List<TotalAmountByCategoryResponseDto> getTotalAmountByCategoryList(String userId);

    List<TotalAmountByCategoryResponseDto> getTotalAmountByCategoryList();

    Map<Category, Long> findTotalAmountMapByUserAndBudgetYearMonth(String userId, YearMonth budgetYearMonth);
}
