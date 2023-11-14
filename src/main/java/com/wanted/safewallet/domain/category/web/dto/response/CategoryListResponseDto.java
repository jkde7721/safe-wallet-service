package com.wanted.safewallet.domain.category.web.dto.response;

import com.wanted.safewallet.domain.category.persistence.entity.CategoryType;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CategoryListResponseDto {

    private List<CategoryResponseDto> categoryList;

    @Getter
    @AllArgsConstructor
    public static class CategoryResponseDto {

        private Long categoryId;
        private CategoryType type;
    }
}
