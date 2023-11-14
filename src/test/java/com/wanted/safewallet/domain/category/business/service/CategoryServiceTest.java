package com.wanted.safewallet.domain.category.business.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.wanted.safewallet.domain.category.business.mapper.CategoryMapper;
import com.wanted.safewallet.domain.category.persistence.entity.Category;
import com.wanted.safewallet.domain.category.persistence.entity.CategoryType;
import com.wanted.safewallet.domain.category.persistence.repository.CategoryRepository;
import com.wanted.safewallet.domain.category.web.dto.response.CategoryListResponseDto;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @InjectMocks
    CategoryService categoryService;

    @Mock
    CategoryRepository categoryRepository;

    @Spy
    CategoryMapper categoryMapper;

    @DisplayName("전체 카테고리 조회 테스트 : 성공")
    @Test
    void getCategoryList() {
        //given
        List<Category> categoryList = List.of(Category.builder().id(1L).type(CategoryType.FOOD).build(),
            Category.builder().id(2L).type(CategoryType.TRAFFIC).build());
        given(categoryRepository.findAll()).willReturn(categoryList);

        //when
        CategoryListResponseDto responseDto = categoryService.getCategoryList();

        //then
        then(categoryRepository).should(times(1)).findAll();
        then(categoryMapper).should(times(1)).toDto(categoryList);
        assertThat(responseDto.getCategoryList()).hasSize(2);
        assertThat(responseDto.getCategoryList())
            .extracting("categoryId")
            .contains(1L, 2L);
        assertThat(responseDto.getCategoryList())
            .extracting("type")
            .contains(CategoryType.FOOD, CategoryType.TRAFFIC);
    }
}
