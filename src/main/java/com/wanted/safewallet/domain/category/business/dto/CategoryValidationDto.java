package com.wanted.safewallet.domain.category.business.dto;

import com.wanted.safewallet.domain.category.persistence.entity.CategoryType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CategoryValidationDto {

    private Long categoryId;
    private CategoryType type;
}
