package com.wanted.safewallet.domain.budget.business.facade;

import static com.wanted.safewallet.domain.category.persistence.entity.CategoryType.FOOD;
import static com.wanted.safewallet.domain.category.persistence.entity.CategoryType.TRAFFIC;
import static com.wanted.safewallet.utils.Fixtures.aBudget;
import static com.wanted.safewallet.utils.Fixtures.aCategory;
import static com.wanted.safewallet.utils.Fixtures.anUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.wanted.safewallet.domain.budget.business.dto.BudgetUpdateDto;
import com.wanted.safewallet.domain.budget.business.mapper.BudgetMapper;
import com.wanted.safewallet.domain.budget.business.service.BudgetService;
import com.wanted.safewallet.domain.budget.persistence.entity.Budget;
import com.wanted.safewallet.domain.budget.web.dto.request.BudgetSetUpRequest;
import com.wanted.safewallet.domain.budget.web.dto.request.BudgetSetUpRequest.BudgetOfCategoryRequest;
import com.wanted.safewallet.domain.budget.web.dto.request.BudgetUpdateRequest;
import com.wanted.safewallet.domain.budget.web.dto.response.BudgetSetUpResponse;
import com.wanted.safewallet.domain.budget.web.dto.response.BudgetUpdateResponse;
import com.wanted.safewallet.domain.category.business.dto.CategoryValidationDto;
import com.wanted.safewallet.domain.category.business.service.CategoryService;
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
class BudgetFacadeServiceTest {

    @InjectMocks
    BudgetFacadeService budgetFacadeService;

    @Spy
    BudgetMapper budgetMapper;

    @Mock
    BudgetService budgetService;

    @Mock
    CategoryService categoryService;

    @DisplayName("월별 예산 설정 퍼사드 서비스 테스트 : 성공")
    @Test
    void setUpBudget() {
        //given
        String userId = "testUserId";
        YearMonth budgetYearMonth = YearMonth.of(2023, 11);
        BudgetSetUpRequest request = new BudgetSetUpRequest(budgetYearMonth,
            List.of(new BudgetOfCategoryRequest(1L, FOOD, 10000L),
                new BudgetOfCategoryRequest(2L, TRAFFIC, 5000L)));
        List<Budget> savedBudgetList = List.of(
            aBudget().id(1L)
                .user(anUser().id(userId).build())
                .category(aCategory().id(1L).type(FOOD).build())
                .amount(10000L).budgetYearMonth(budgetYearMonth).build(),
            aBudget().id(2L)
                .user(anUser().id(userId).build())
                .category(aCategory().id(2L).type(TRAFFIC).build())
                .amount(5000L).budgetYearMonth(budgetYearMonth).build());
        given(budgetService.saveBudgetList(anyList())).willReturn(savedBudgetList);

        //when
        BudgetSetUpResponse response = budgetFacadeService.setUpBudget(userId, request);

        //then
        then(categoryService).should(times(1)).validateCategory(anyList());
        then(budgetService).should(times(1))
            .checkForDuplicatedBudget(anyString(), any(YearMonth.class), anyList());
        then(budgetService).should(times(1)).saveBudgetList(anyList());
        assertThat(response.getBudgetList()).hasSize(2);
        assertThat(response.getBudgetList()).extracting("categoryId").contains(1L, 2L);
        assertThat(response.getBudgetList()).extracting("type").contains(FOOD, TRAFFIC);
        assertThat(response.getBudgetList()).extracting("amount").contains(10000L, 5000L);
    }

    @DisplayName("월별 예산 수정 퍼사드 서비스 테스트 : 성공")
    @Test
    void updateBudget() {
        //given
        String userId = "testUserId";
        Long budgetId = 1L;
        long amount = 10000;
        YearMonth budgetYearMonth = YearMonth.now();
        BudgetUpdateRequest request = new BudgetUpdateRequest(budgetYearMonth, 1L, FOOD, amount);
        Budget updatedBudget = aBudget().id(budgetId)
            .user(anUser().id(userId).build())
            .category(aCategory().id(1L).type(FOOD).build())
            .amount(amount).budgetYearMonth(budgetYearMonth).build();
        given(budgetService.updateBudget(anyString(), anyLong(), any(BudgetUpdateDto.class))).willReturn(updatedBudget);

        //when
        BudgetUpdateResponse response = budgetFacadeService.updateBudget(userId, budgetId, request);

        //then
        then(categoryService).should(times(1)).validateCategory(any(CategoryValidationDto.class));
        then(budgetService).should(times(1))
            .updateBudget(anyString(), anyLong(), any(BudgetUpdateDto.class));
        assertThat(response.getBudgetId()).isEqualTo(budgetId);
        assertThat(response.getBudgetYearMonth()).isEqualTo(request.getBudgetYearMonth());
        assertThat(response.getCategoryId()).isEqualTo(request.getCategoryId());
        assertThat(response.getType()).isEqualTo(request.getType());
        assertThat(response.getAmount()).isEqualTo(request.getAmount());
    }
}
