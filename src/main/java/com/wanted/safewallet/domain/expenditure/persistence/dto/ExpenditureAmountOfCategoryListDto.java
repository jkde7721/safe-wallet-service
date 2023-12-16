package com.wanted.safewallet.domain.expenditure.persistence.dto;

import static java.util.stream.Collectors.toMap;

import com.wanted.safewallet.domain.category.persistence.entity.Category;
import java.util.List;
import java.util.Map;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter(AccessLevel.PRIVATE)
@AllArgsConstructor
public class ExpenditureAmountOfCategoryListDto {

    List<ExpenditureAmountOfCategoryDto> expenditureAmountOfCategoryList;

    @Getter(AccessLevel.PRIVATE)
    @AllArgsConstructor
    public static class ExpenditureAmountOfCategoryDto {

        private Category category;

        private Long amount;
    }

    public Map<Category, Long> toMapByCategory() {
        return expenditureAmountOfCategoryList.stream()
            .collect(toMap(ExpenditureAmountOfCategoryDto::getCategory,
                ExpenditureAmountOfCategoryDto::getAmount));
    }
}
