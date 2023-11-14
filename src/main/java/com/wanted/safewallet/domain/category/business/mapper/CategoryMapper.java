package com.wanted.safewallet.domain.category.business.mapper;

import com.wanted.safewallet.domain.category.persistence.entity.Category;
import com.wanted.safewallet.domain.category.web.dto.response.CategoryListResponseDto;
import com.wanted.safewallet.domain.category.web.dto.response.CategoryListResponseDto.CategoryResponseDto;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public CategoryListResponseDto toDto(List<Category> categoryList) {
        return new CategoryListResponseDto(categoryList.stream()
            .map(c -> new CategoryResponseDto(c.getId(), c.getType()))
            .toList());
    }
}
