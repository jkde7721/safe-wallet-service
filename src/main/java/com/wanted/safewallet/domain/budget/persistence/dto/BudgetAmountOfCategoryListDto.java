package com.wanted.safewallet.domain.budget.persistence.dto;

import static java.util.stream.Collectors.toMap;

import com.wanted.safewallet.domain.category.persistence.entity.Category;
import java.util.List;
import java.util.Map;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter(AccessLevel.PRIVATE)
@AllArgsConstructor
public class BudgetAmountOfCategoryListDto {

    private List<BudgetAmountOfCategoryDto> budgetAmountOfCategoryList;

    @Getter(AccessLevel.PRIVATE)
    @AllArgsConstructor
    public static class BudgetAmountOfCategoryDto {

        private Category category;

        private Long amount;
    }

    public Map<Category, Long> toMapByCategory() {
        return budgetAmountOfCategoryList.stream()
            .collect(toMap(BudgetAmountOfCategoryDto::getCategory,
                BudgetAmountOfCategoryDto::getAmount));
    }
}
