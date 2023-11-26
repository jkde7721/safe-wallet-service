package com.wanted.safewallet.domain.expenditure.persistence.dto.response;

import com.wanted.safewallet.domain.category.persistence.entity.CategoryType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StatsByCategoryResponseDto {

    private Long categoryId;

    private CategoryType type;

    private Long totalAmount;
}
