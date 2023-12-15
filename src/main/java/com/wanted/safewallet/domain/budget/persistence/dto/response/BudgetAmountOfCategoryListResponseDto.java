package com.wanted.safewallet.domain.budget.persistence.dto.response;

import static java.util.stream.Collectors.toMap;

import com.wanted.safewallet.domain.category.persistence.entity.Category;
import java.util.List;
import java.util.Map;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter(AccessLevel.PRIVATE)
@AllArgsConstructor
public class BudgetAmountOfCategoryListResponseDto {

    private List<BudgetAmountOfCategoryResponseDto> budgetAmountOfCategoryList;

    @Getter(AccessLevel.PRIVATE)
    @AllArgsConstructor
    public static class BudgetAmountOfCategoryResponseDto {

        private Category category;

        private Long amount;
    }

    public Map<Category, Long> toMapByCategory() {
        return budgetAmountOfCategoryList.stream()
            .collect(toMap(BudgetAmountOfCategoryResponseDto::getCategory,
                BudgetAmountOfCategoryResponseDto::getAmount));
    }
}
