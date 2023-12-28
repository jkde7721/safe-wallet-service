package com.wanted.safewallet.domain.category.web.controller;

import com.wanted.safewallet.domain.category.business.facade.CategoryFacadeService;
import com.wanted.safewallet.domain.category.web.dto.response.CategoryListResponse;
import com.wanted.safewallet.global.dto.response.aop.CommonResponseContent;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CommonResponseContent
@RequiredArgsConstructor
@RequestMapping("/api/categories")
@RestController
public class CategoryController {

    private final CategoryFacadeService categoryFacadeService;

    @GetMapping
    public CategoryListResponse getCategoryList() {
        return categoryFacadeService.getCategoryList();
    }
}
