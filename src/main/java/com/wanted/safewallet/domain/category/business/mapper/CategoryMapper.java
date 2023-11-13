package com.wanted.safewallet.domain.category.business.mapper;

import com.wanted.safewallet.domain.category.persistence.entity.Category;
import com.wanted.safewallet.domain.category.web.dto.response.CategoryListResponseDto;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public CategoryListResponseDto toDto(List<Category> categoryList) {
        List<String> categoryTypeList = categoryList.stream().map(c -> c.getType().name()).toList();
        return new CategoryListResponseDto(categoryTypeList);
    }
}
