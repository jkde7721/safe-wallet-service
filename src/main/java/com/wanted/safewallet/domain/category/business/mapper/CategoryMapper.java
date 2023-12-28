package com.wanted.safewallet.domain.category.business.mapper;

import com.wanted.safewallet.domain.category.persistence.entity.Category;
import com.wanted.safewallet.domain.category.web.dto.response.CategoryListResponse;
import com.wanted.safewallet.domain.category.web.dto.response.CategoryListResponse.CategoryResponse;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public CategoryListResponse toResponse(List<Category> categoryList) {
        return new CategoryListResponse(categoryList.stream()
            .map(c -> new CategoryResponse(c.getId(), c.getType()))
            .toList());
    }
}
