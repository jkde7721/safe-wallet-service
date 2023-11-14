package com.wanted.safewallet.domain.category.business.service;

import static com.wanted.safewallet.global.exception.ErrorCode.NOT_FOUND_CATEGORY;

import com.wanted.safewallet.domain.category.business.dto.request.CategoryValidRequestDto;
import com.wanted.safewallet.domain.category.business.mapper.CategoryMapper;
import com.wanted.safewallet.domain.category.persistence.entity.Category;
import com.wanted.safewallet.domain.category.persistence.repository.CategoryRepository;
import com.wanted.safewallet.domain.category.web.dto.response.CategoryListResponseDto;
import com.wanted.safewallet.global.exception.BusinessException;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class CategoryService {

    private final CategoryMapper categoryMapper;
    private final CategoryRepository categoryRepository;

    public CategoryListResponseDto getCategoryList() {
        List<Category> categoryList = categoryRepository.findAll();
        return categoryMapper.toDto(categoryList);
    }

    public void validateCategory(List<CategoryValidRequestDto> requestDtoList) {
        Map<Long, Category> categoryMap = categoryRepository.findAllMap();
        requestDtoList.forEach(category -> {
            if (!existsCategory(categoryMap, category)) {
                throw new BusinessException(NOT_FOUND_CATEGORY);
            }
        });
    }

    private boolean existsCategory(Map<Long, Category> categoryMap, CategoryValidRequestDto category) {
        return categoryMap.containsKey(category.getCategoryId())
            && categoryMap.get(category.getCategoryId()).getType() == category.getType();
    }
}
