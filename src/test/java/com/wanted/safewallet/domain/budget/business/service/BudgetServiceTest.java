package com.wanted.safewallet.domain.budget.business.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.wanted.safewallet.domain.budget.business.mapper.BudgetMapper;
import com.wanted.safewallet.domain.budget.persistence.repository.BudgetRepository;
import com.wanted.safewallet.domain.budget.web.dto.request.BudgetSetUpRequestDto;
import com.wanted.safewallet.domain.budget.web.dto.request.BudgetSetUpRequestDto.BudgetByCategory;
import com.wanted.safewallet.domain.budget.web.dto.response.BudgetSetUpResponseDto;
import com.wanted.safewallet.domain.category.business.service.CategoryService;
import com.wanted.safewallet.domain.category.persistence.entity.CategoryType;
import java.time.YearMonth;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BudgetServiceTest {

    @InjectMocks
    BudgetService budgetService;

    @Mock
    CategoryService categoryService;

    @Mock
    BudgetRepository budgetRepository;

    @Spy
    BudgetMapper budgetMapper;

    @DisplayName("월별 예산 설정 서비스 테스트 : 성공")
    @Test
    void setUpBudget() {
        //given
        given(budgetRepository.existsByUserIdAndBudgetYearMonthAndInCategories(
            anyString(), any(YearMonth.class), anyList())).willReturn(false);

        //when
        String userId = "testUserId";
        BudgetSetUpRequestDto requestDto = new BudgetSetUpRequestDto(YearMonth.of(2023, 11),
            List.of(new BudgetByCategory(1L, CategoryType.FOOD, 10000L),
                new BudgetByCategory(2L, CategoryType.TRAFFIC, 5000L)));
        BudgetSetUpResponseDto responseDto = budgetService.setUpBudget(userId, requestDto);

        //then
        then(categoryService).should(times(1)).validateCategory(anyList());
        then(budgetRepository).should(times(1))
            .existsByUserIdAndBudgetYearMonthAndInCategories(anyString(), any(YearMonth.class), anyList());
        then(budgetRepository).should(times(1)).saveAll(anyList());

        assertThat(responseDto.getBudgetList()).hasSize(2);
        assertThat(responseDto.getBudgetList()).extracting("categoryId").contains(1L, 2L);
        assertThat(responseDto.getBudgetList()).extracting("type")
            .contains(CategoryType.FOOD, CategoryType.TRAFFIC);
        assertThat(responseDto.getBudgetList()).extracting("amount").contains(10000L, 5000L);
    }

    @DisplayName("월별 예산 설정 서비스 테스트 : 실패 - 같은 날짜, 같은 카테고리의 기존 예산 설정 내역 존재")
    @Test
    void setUpBudgetFail() {
        //given
        given(budgetRepository.existsByUserIdAndBudgetYearMonthAndInCategories(
            anyString(), any(YearMonth.class), anyList())).willReturn(true);

        //when
        String userId = "testUserId";
        BudgetSetUpRequestDto requestDto = new BudgetSetUpRequestDto(YearMonth.of(2023, 11),
            List.of(new BudgetByCategory(1L, CategoryType.FOOD, 10000L),
                new BudgetByCategory(2L, CategoryType.TRAFFIC, 5000L)));

        //then
        assertThatThrownBy(() -> budgetService.setUpBudget(userId, requestDto))
                .isInstanceOf(RuntimeException.class);
        then(categoryService).should(times(1)).validateCategory(anyList());
        then(budgetRepository).should(times(1))
            .existsByUserIdAndBudgetYearMonthAndInCategories(anyString(), any(YearMonth.class), anyList());
        then(budgetRepository).should(times(0)).saveAll(anyList());
    }
}
