package com.wanted.safewallet.domain.budget.business.service;

import static com.wanted.safewallet.domain.category.persistence.entity.CategoryType.CLOTHING;
import static com.wanted.safewallet.domain.category.persistence.entity.CategoryType.ETC;
import static com.wanted.safewallet.domain.category.persistence.entity.CategoryType.FOOD;
import static com.wanted.safewallet.domain.category.persistence.entity.CategoryType.LEISURE;
import static com.wanted.safewallet.domain.category.persistence.entity.CategoryType.RESIDENCE;
import static com.wanted.safewallet.domain.category.persistence.entity.CategoryType.TRAFFIC;
import static com.wanted.safewallet.global.exception.ErrorCode.ALREADY_EXISTS_BUDGET;
import static com.wanted.safewallet.global.exception.ErrorCode.FORBIDDEN_BUDGET;
import static com.wanted.safewallet.global.exception.ErrorCode.NOT_FOUND_BUDGET;
import static com.wanted.safewallet.utils.Fixtures.aBudget;
import static com.wanted.safewallet.utils.Fixtures.aCategory;
import static com.wanted.safewallet.utils.Fixtures.anUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.anyList;
import static org.mockito.BDDMockito.anyLong;
import static org.mockito.BDDMockito.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.times;

import com.wanted.safewallet.domain.budget.business.dto.BudgetUpdateDto;
import com.wanted.safewallet.domain.budget.persistence.dto.BudgetAmountOfCategoryListDto;
import com.wanted.safewallet.domain.budget.persistence.dto.BudgetAmountOfCategoryListDto.BudgetAmountOfCategoryDto;
import com.wanted.safewallet.domain.budget.persistence.entity.Budget;
import com.wanted.safewallet.domain.budget.persistence.repository.BudgetRepository;
import com.wanted.safewallet.domain.category.persistence.entity.Category;
import com.wanted.safewallet.global.exception.BusinessException;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BudgetServiceTest {

    @InjectMocks
    BudgetService budgetService;

    @Mock
    BudgetRepository budgetRepository;

    @DisplayName("월별 예산 수정 서비스 테스트 : 성공 - 수정하려는 카테고리, 년월의 기존 예산 내역 존재하지 않는 경우")
    @Test
    void updateBudget() {
        //given
        String userId = "testUserId";
        Long budgetId = 1L;
        long amount = 10000;
        YearMonth now = YearMonth.now();
        Budget budget = aBudget().id(budgetId)
            .user(anUser().id(userId).build())
            .amount(amount).budgetYearMonth(now).build();
        given(budgetRepository.findById(anyLong())).willReturn(Optional.of(budget));
        given(budgetRepository.findByUserAndCategoryAndBudgetYearMonthFetch(anyString(), anyLong(), any(YearMonth.class)))
            .willReturn(Optional.empty());

        //when
        BudgetUpdateDto updateDto = new BudgetUpdateDto(now.plusMonths(1),
            2L, TRAFFIC, amount * 2);
        Budget updatedBudget = budgetService.updateBudget(userId, budgetId, updateDto);

        //then
        assertThat(updatedBudget.getId()).isEqualTo(budgetId);
        assertThat(updatedBudget.getBudgetYearMonth()).isEqualTo(updateDto.getBudgetYearMonth());
        assertThat(updatedBudget.getCategory().getId()).isEqualTo(updateDto.getCategoryId());
        assertThat(updatedBudget.getCategory().getType()).isEqualTo(updateDto.getType());
        assertThat(updatedBudget.getAmount()).isEqualTo(updateDto.getAmount());
    }

    @DisplayName("월별 예산 수정 서비스 테스트 : 성공 - 수정하려는 카테고리, 년월의 기존 예산 내역 존재하는 경우(기존 예산 내역에 금액 추가)")
    @Test
    void updateBudget_exists() {
        //given
        String userId = "testUserId";
        Long budgetId = 1L;
        Long anotherBudgetId = 2L;
        Long amount = 10000L;
        YearMonth now = YearMonth.now();
        Budget budget = aBudget().id(budgetId)
            .user(anUser().id(userId).build())
            .category(aCategory().id(1L).type(FOOD).build())
            .amount(amount).budgetYearMonth(now).build();
        Budget anotherBudget = aBudget().id(anotherBudgetId)
            .user(anUser().id(userId).build())
            .category(aCategory().id(2L).type(TRAFFIC).build())
            .amount(amount).budgetYearMonth(now).build();
        given(budgetRepository.findById(anyLong())).willReturn(Optional.of(budget));
        given(budgetRepository.findByUserAndCategoryAndBudgetYearMonthFetch(anyString(), anyLong(), any(YearMonth.class)))
            .willReturn(Optional.of(anotherBudget));

        //when
        BudgetUpdateDto updateDto = new BudgetUpdateDto(now, 2L, TRAFFIC, 20000L);
        Budget updatedBudget = budgetService.updateBudget(userId, budgetId, updateDto);

        //then
        assertThat(updatedBudget.getId()).isEqualTo(anotherBudgetId);
        assertThat(updatedBudget.getBudgetYearMonth()).isEqualTo(anotherBudget.getBudgetYearMonth());
        assertThat(updatedBudget.getCategory().getId()).isEqualTo(anotherBudget.getCategory().getId());
        assertThat(updatedBudget.getCategory().getType()).isEqualTo(anotherBudget.getCategory().getType());
        assertThat(updatedBudget.getAmount()).isEqualTo(amount + updateDto.getAmount());
        then(budgetRepository).should(times(1)).deleteById(budgetId);
    }

    @DisplayName("월별 예산 설계 서비스 테스트 : 성공 - 현재 로그인한 사용자의 예산 내역 기반으로 설계")
    @Test
    void consultBudget_withMyBudgets() {
        //given
        String userId = "testUserId";
        Long totalAmount = 1000_000L;
        List<BudgetAmountOfCategoryDto> budgetAmountOfCategoryList = List.of(
            new BudgetAmountOfCategoryDto(aCategory().id(1L).type(FOOD).build(), 150_000L),
            new BudgetAmountOfCategoryDto(aCategory().id(2L).type(TRAFFIC).build(), 100_000L),
            new BudgetAmountOfCategoryDto(aCategory().id(3L).type(RESIDENCE).build(), 500_000L),
            new BudgetAmountOfCategoryDto(aCategory().id(4L).type(CLOTHING).build(), 100_000L),
            new BudgetAmountOfCategoryDto(aCategory().id(5L).type(LEISURE).build(), 50_000L),
            new BudgetAmountOfCategoryDto(aCategory().id(6L).type(ETC).build(), 5_000L));
        BudgetAmountOfCategoryListDto budgetAmountOfCategoryListDto = new BudgetAmountOfCategoryListDto(budgetAmountOfCategoryList);
        given(budgetRepository.existsByUser(anyString())).willReturn(true);
        given(budgetRepository.findBudgetAmountOfCategoryListByUserAndBudgetYearMonth(anyString(), isNull(YearMonth.class)))
            .willReturn(budgetAmountOfCategoryListDto);

        //when
        Map<Category, Long> consultedBudgetAmountByCategory = budgetService.consultBudget(userId, totalAmount);

        //then
        then(budgetRepository).should(times(1)).existsByUser(anyString());
        then(budgetRepository).should(times(1)).findBudgetAmountOfCategoryListByUserAndBudgetYearMonth(anyString(), isNull(YearMonth.class));
        assertThat(consultedBudgetAmountByCategory).containsExactlyInAnyOrderEntriesOf(Map.of(
            aCategory().type(FOOD).build(), 165700L,
            aCategory().type(TRAFFIC).build(), 110400L,
            aCategory().type(RESIDENCE).build(), 552400L,
            aCategory().type(CLOTHING).build(), 110400L,
            aCategory().type(LEISURE).build(), 0L,
            aCategory().type(ETC).build(), 61100L));
    }

    @DisplayName("중복 예산 존재 여부 확인 테스트 : 실패 - 같은 날짜, 같은 카테고리의 기존 예산 설정 내역 존재")
    @Test
    void checkForDuplicatedBudget_fail() {
        //given
        given(budgetRepository.existsByUserAndBudgetYearMonthAndCategories(
            anyString(), any(YearMonth.class), anyList())).willReturn(true);

        //when, then
        String userId = "testUserId";
        YearMonth budgetYearMonth = YearMonth.of(2023, 11);
        List<Long> categoryIds = List.of(1L, 2L);
        assertThatThrownBy(() -> budgetService.checkForDuplicatedBudget(userId, budgetYearMonth, categoryIds))
            .isInstanceOf(BusinessException.class)
            .extracting("errorCode").isEqualTo(ALREADY_EXISTS_BUDGET);
    }

    @DisplayName("유효한 예산 내역 조회 서비스 테스트 : 실패 - 현재 로그인한 사용자의 예산이 아님")
    @Test
    void getValidBudget_fail() {
        //given
        String userId = "testUserId";
        String anotherUserId = "otherUserId";
        Long budgetId = 1L;
        Budget budget = aBudget().id(budgetId)
            .user(anUser().id(anotherUserId).build()).build();
        given(budgetRepository.findById(anyLong())).willReturn(Optional.of(budget));

        //when, then
        assertThatThrownBy(() -> budgetService.getValidBudget(userId, budgetId))
            .isInstanceOf(BusinessException.class)
            .extracting("errorCode").isEqualTo(FORBIDDEN_BUDGET);
        then(budgetRepository).should(times(1)).findById(anyLong());
    }

    @DisplayName("예산 내역 조회 서비스 테스트 : 실패 - 해당 예산 존재하지 않음")
    @Test
    void getBudget_fail() {
        //given
        Long budgetId = 1L;
        given(budgetRepository.findById(anyLong())).willReturn(Optional.empty());

        //when, then
        assertThatThrownBy(() -> budgetService.getBudget(budgetId))
            .isInstanceOf(BusinessException.class)
            .extracting("errorCode").isEqualTo(NOT_FOUND_BUDGET);
        then(budgetRepository).should(times(1)).findById(anyLong());
    }
}
