package com.wanted.safewallet.domain.category.business.facade;

import com.wanted.safewallet.domain.category.business.mapper.CategoryMapper;
import com.wanted.safewallet.domain.category.business.service.CategoryService;
import com.wanted.safewallet.domain.category.persistence.entity.Category;
import com.wanted.safewallet.domain.category.web.dto.response.CategoryListResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class CategoryFacadeService {

    private final CategoryMapper categoryMapper;
    private final CategoryService categoryService;

    public CategoryListResponse getCategoryList() {
        List<Category> categoryList = categoryService.getCategoryList();
        return categoryMapper.toDto(categoryList);
    }
}
