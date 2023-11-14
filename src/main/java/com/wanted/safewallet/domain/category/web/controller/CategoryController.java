package com.wanted.safewallet.domain.category.web.controller;

import com.wanted.safewallet.domain.category.business.service.CategoryService;
import com.wanted.safewallet.domain.category.web.dto.response.CategoryListResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/categories")
@RestController
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<CategoryListResponseDto> getCategoryList() {
        CategoryListResponseDto responseDto = categoryService.getCategoryList();
        return ResponseEntity.ok(responseDto);
    }
}
