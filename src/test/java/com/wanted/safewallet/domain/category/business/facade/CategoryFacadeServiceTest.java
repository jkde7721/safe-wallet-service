package com.wanted.safewallet.domain.category.business.facade;

import static com.wanted.safewallet.domain.category.persistence.entity.CategoryType.FOOD;
import static com.wanted.safewallet.domain.category.persistence.entity.CategoryType.TRAFFIC;
import static com.wanted.safewallet.utils.Fixtures.aCategory;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.wanted.safewallet.domain.category.business.mapper.CategoryMapper;
import com.wanted.safewallet.domain.category.business.service.CategoryService;
import com.wanted.safewallet.domain.category.persistence.entity.Category;
import com.wanted.safewallet.domain.category.web.dto.response.CategoryListResponse;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CategoryFacadeServiceTest {

    @InjectMocks
    CategoryFacadeService categoryFacadeService;

    @Mock
    CategoryService categoryService;

    @Spy
    CategoryMapper categoryMapper;

    @DisplayName("전체 카테고리 조회 테스트 : 성공")
    @Test
    void getCategoryList() {
        //given
        List<Category> categoryList = List.of(aCategory().id(1L).type(FOOD).build(),
            aCategory().id(2L).type(TRAFFIC).build());
        given(categoryService.getCategoryList()).willReturn(categoryList);

        //when
        CategoryListResponse response = categoryFacadeService.getCategoryList();

        //then
        then(categoryService).should(times(1)).getCategoryList();
        then(categoryMapper).should(times(1)).toDto(categoryList);
        assertThat(response.getCategoryList()).hasSize(2);
        assertThat(response.getCategoryList())
            .extracting("categoryId").contains(1L, 2L);
        assertThat(response.getCategoryList())
            .extracting("type").contains(FOOD, TRAFFIC);
    }
}