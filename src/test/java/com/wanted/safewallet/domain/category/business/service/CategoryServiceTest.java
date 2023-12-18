package com.wanted.safewallet.domain.category.business.service;

import static com.wanted.safewallet.domain.category.persistence.entity.CategoryType.FOOD;
import static com.wanted.safewallet.domain.category.persistence.entity.CategoryType.TRAFFIC;
import static com.wanted.safewallet.global.exception.ErrorCode.NOT_FOUND_CATEGORY;
import static com.wanted.safewallet.utils.Fixtures.aCategory;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.wanted.safewallet.domain.category.business.dto.CategoryValidationDto;
import com.wanted.safewallet.domain.category.persistence.entity.Category;
import com.wanted.safewallet.domain.category.persistence.repository.CategoryRepository;
import com.wanted.safewallet.global.exception.BusinessException;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @InjectMocks
    CategoryService categoryService;

    @Mock
    CategoryRepository categoryRepository;

    @DisplayName("카테고리 유효성 검증 테스트 : 실패 - 존재하지 않는 카테고리")
    @Test
    void validateCategoryFail() {
        //given
        Map<Long, Category> categoryMap = Map.of(
            1L, aCategory().id(1L).type(FOOD).build(),
            2L, aCategory().id(2L).type(TRAFFIC).build());
        given(categoryRepository.findAllMap()).willReturn(categoryMap);

        //when
        List<CategoryValidationDto> categoryValidationDtoList = List.of(
            new CategoryValidationDto(1L, TRAFFIC));

        //then
        assertThatThrownBy(() -> categoryService.validateCategory(categoryValidationDtoList))
            .isInstanceOf(BusinessException.class)
            .extracting("errorCode").isEqualTo(NOT_FOUND_CATEGORY);
        then(categoryRepository).should(times(1)).findAllMap();
    }
}
