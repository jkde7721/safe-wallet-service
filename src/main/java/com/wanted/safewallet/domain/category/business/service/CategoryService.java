package com.wanted.safewallet.domain.category.business.service;

import com.wanted.safewallet.domain.category.business.mapper.CategoryMapper;
import com.wanted.safewallet.domain.category.persistence.entity.Category;
import com.wanted.safewallet.domain.category.persistence.repository.CategoryRepository;
import com.wanted.safewallet.domain.category.web.dto.response.CategoryListResponseDto;
import java.util.List;
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
}
