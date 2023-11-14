package com.wanted.safewallet.domain.category.business.dto.request;

import com.wanted.safewallet.domain.category.persistence.entity.CategoryType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CategoryValidRequestDto {

    private Long categoryId;
    private CategoryType type;
}
