package com.wanted.safewallet.domain.category.business.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.wanted.safewallet.domain.category.business.dto.request.CategoryValidRequestDto;
import com.wanted.safewallet.domain.category.business.mapper.CategoryMapper;
import com.wanted.safewallet.domain.category.persistence.entity.Category;
import com.wanted.safewallet.domain.category.persistence.entity.CategoryType;
import com.wanted.safewallet.domain.category.persistence.repository.CategoryRepository;
import com.wanted.safewallet.domain.category.web.dto.response.CategoryListResponseDto;
import java.util.List;
import java.util.Map;
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

    @DisplayName("카테고리 유효성 검증 테스트 : 실패 - 존재하지 않는 카테고리")
    @Test
    void validateCategoryFail() {
        //given
        Map<Long, Category> categoryMap = Map.of(
            1L, Category.builder().id(1L).type(CategoryType.FOOD).build(),
            2L, Category.builder().id(2L).type(CategoryType.TRAFFIC).build());
        given(categoryRepository.findAllMap()).willReturn(categoryMap);

        //when
        List<CategoryValidRequestDto> categoryValidRequestDtoList = List.of(
            new CategoryValidRequestDto(1L, CategoryType.TRAFFIC));

        //then
        assertThatThrownBy(() -> categoryService.validateCategory(categoryValidRequestDtoList))
            .isInstanceOf(RuntimeException.class);
        then(categoryRepository).should(times(1)).findAllMap();
    }
}
