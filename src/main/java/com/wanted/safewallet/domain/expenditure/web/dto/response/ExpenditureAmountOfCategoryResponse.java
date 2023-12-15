package com.wanted.safewallet.domain.expenditure.web.dto.response;

import com.wanted.safewallet.domain.category.persistence.entity.CategoryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ExpenditureAmountOfCategoryResponse {

    private Long categoryId;

    private CategoryType type;

    private Long amount;
}
