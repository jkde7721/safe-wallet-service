package com.wanted.safewallet.domain.category.web.dto.response;

import com.wanted.safewallet.domain.category.persistence.entity.CategoryType;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CategoryListResponse {

    private List<CategoryResponse> categoryList;

    @Getter
    @AllArgsConstructor
    public static class CategoryResponse {

        private Long categoryId;
        private CategoryType type;
    }
}
