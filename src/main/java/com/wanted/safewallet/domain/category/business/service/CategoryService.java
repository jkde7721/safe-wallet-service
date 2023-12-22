package com.wanted.safewallet.domain.category.business.service;

import static com.wanted.safewallet.global.exception.ErrorCode.NOT_FOUND_CATEGORY;

import com.wanted.safewallet.domain.category.business.dto.CategoryValidationDto;
import com.wanted.safewallet.domain.category.persistence.entity.Category;
import com.wanted.safewallet.domain.category.persistence.repository.CategoryRepository;
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

    private final CategoryRepository categoryRepository;

    public List<Category> getCategoryList() {
        return categoryRepository.findAll();
    }

    public void validateCategory(CategoryValidationDto categoryValidationDto) {
        Map<Long, Category> categoryMap = categoryRepository.findAllMap();
        if (!existsCategory(categoryMap, categoryValidationDto)) {
            throw new BusinessException(NOT_FOUND_CATEGORY);
        }
    }

    public void validateCategory(List<CategoryValidationDto> categoryValidationDtoList) {
        Map<Long, Category> categoryMap = categoryRepository.findAllMap();
        categoryValidationDtoList.forEach(categoryValidationDto -> {
            if (!existsCategory(categoryMap, categoryValidationDto)) {
                throw new BusinessException(NOT_FOUND_CATEGORY);
            }
        });
    }

    private boolean existsCategory(Map<Long, Category> categoryMap, CategoryValidationDto categoryValidationDto) {
        return categoryMap.containsKey(categoryValidationDto.getCategoryId())
            && categoryMap.get(categoryValidationDto.getCategoryId()).getType() == categoryValidationDto.getType();
    }
}
