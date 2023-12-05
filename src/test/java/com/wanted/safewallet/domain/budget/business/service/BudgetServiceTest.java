package com.wanted.safewallet.domain.budget.business.service;

import static com.wanted.safewallet.global.exception.ErrorCode.ALREADY_EXISTS_BUDGET;
import static com.wanted.safewallet.global.exception.ErrorCode.FORBIDDEN_BUDGET;
import static com.wanted.safewallet.global.exception.ErrorCode.NOT_FOUND_BUDGET;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.anyList;
import static org.mockito.BDDMockito.anyLong;
import static org.mockito.BDDMockito.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.times;

import com.wanted.safewallet.domain.budget.business.mapper.BudgetMapper;
import com.wanted.safewallet.domain.budget.persistence.entity.Budget;
import com.wanted.safewallet.domain.budget.persistence.repository.BudgetRepository;
import com.wanted.safewallet.domain.budget.web.dto.request.BudgetSetUpRequestDto;
import com.wanted.safewallet.domain.budget.web.dto.request.BudgetSetUpRequestDto.BudgetByCategory;
import com.wanted.safewallet.domain.budget.web.dto.request.BudgetUpdateRequestDto;
import com.wanted.safewallet.domain.budget.web.dto.response.BudgetSetUpResponseDto;
import com.wanted.safewallet.domain.budget.web.dto.response.BudgetUpdateResponseDto;
import com.wanted.safewallet.domain.category.business.service.CategoryService;
import com.wanted.safewallet.domain.category.persistence.entity.Category;
import com.wanted.safewallet.domain.category.persistence.entity.CategoryType;
import com.wanted.safewallet.domain.user.persistence.entity.User;
import com.wanted.safewallet.global.exception.BusinessException;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
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
    void setUpBudget_fail() {
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
            .isInstanceOf(BusinessException.class)
            .extracting("errorCode").isEqualTo(ALREADY_EXISTS_BUDGET);
        then(categoryService).should(times(1)).validateCategory(anyList());
        then(budgetRepository).should(times(1))
            .existsByUserIdAndBudgetYearMonthAndInCategories(anyString(), any(YearMonth.class), anyList());
        then(budgetRepository).should(times(0)).saveAll(anyList());
    }

    @DisplayName("월별 예산 수정 서비스 테스트 : 성공 - 수정하려는 카테고리, 년월의 기존 예산 내역 존재하지 않는 경우")
    @Test
    void updateBudget() {
        //given
        String userId = "testUserId";
        Long budgetId = 1L;
        YearMonth now = YearMonth.now();
        Budget budget = Budget.builder().id(budgetId)
            .user(User.builder().id(userId).build())
            .category(Category.builder().id(1L).type(CategoryType.FOOD).build())
            .amount(10000L).budgetYearMonth(now).build();
        given(budgetRepository.findById(anyLong())).willReturn(Optional.of(budget));
        given(budgetRepository.findByUserAndCategoryAndBudgetYearMonthFetch(anyString(), anyLong(), any(YearMonth.class)))
            .willReturn(Optional.empty());

        //when
        BudgetUpdateRequestDto requestDto = new BudgetUpdateRequestDto(now.plusMonths(1),
            2L, CategoryType.TRAFFIC, 20000L);
        BudgetUpdateResponseDto responseDto = budgetService.updateBudget(userId, budgetId, requestDto);

        //then
        assertThat(responseDto.getBudgetId()).isEqualTo(budgetId);
        assertThat(responseDto.getBudgetYearMonth()).isEqualTo(requestDto.getBudgetYearMonth());
        assertThat(responseDto.getCategoryId()).isEqualTo(requestDto.getCategoryId());
        assertThat(responseDto.getType()).isEqualTo(requestDto.getType());
        assertThat(responseDto.getAmount()).isEqualTo(requestDto.getAmount());
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
        Budget budget = Budget.builder().id(budgetId)
            .user(User.builder().id(userId).build())
            .category(Category.builder().id(1L).type(CategoryType.FOOD).build())
            .amount(amount).budgetYearMonth(now).build();
        Budget anotherBudget = Budget.builder().id(anotherBudgetId)
            .user(User.builder().id(userId).build())
            .category(Category.builder().id(2L).type(CategoryType.TRAFFIC).build())
            .amount(amount).budgetYearMonth(now).build();
        given(budgetRepository.findById(anyLong())).willReturn(Optional.of(budget));
        given(budgetRepository.findByUserAndCategoryAndBudgetYearMonthFetch(anyString(), anyLong(), any(YearMonth.class)))
            .willReturn(Optional.of(anotherBudget));

        //when
        BudgetUpdateRequestDto requestDto = new BudgetUpdateRequestDto(now, 2L, CategoryType.TRAFFIC, 20000L);
        BudgetUpdateResponseDto responseDto = budgetService.updateBudget(userId, budgetId, requestDto);

        //then
        assertThat(responseDto.getBudgetId()).isEqualTo(anotherBudgetId);
        assertThat(responseDto.getBudgetYearMonth()).isEqualTo(anotherBudget.getBudgetYearMonth());
        assertThat(responseDto.getCategoryId()).isEqualTo(anotherBudget.getCategory().getId());
        assertThat(responseDto.getType()).isEqualTo(anotherBudget.getCategory().getType());
        assertThat(responseDto.getAmount()).isEqualTo(amount + requestDto.getAmount());
        then(budgetRepository).should(times(1)).deleteById(budgetId);
    }

    @DisplayName("유효한 예산 내역 조회 서비스 테스트 : 실패 - 현재 로그인한 사용자의 예산이 아님")
    @Test
    void getValidBudget_fail() {
        //given
        String userId = "testUserId";
        String anotherUserId = "otherUserId";
        Long budgetId = 1L;
        Budget budget = Budget.builder().id(budgetId)
            .user(User.builder().id(userId).build())
            .category(Category.builder().id(1L).type(CategoryType.FOOD).build())
            .amount(10000L).budgetYearMonth(YearMonth.now()).build();
        given(budgetRepository.findById(anyLong())).willReturn(Optional.of(budget));

        //when, then
        assertThatThrownBy(() -> budgetService.getValidBudget(anotherUserId, budgetId))
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
