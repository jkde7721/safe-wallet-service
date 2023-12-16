package com.wanted.safewallet.domain.expenditure.business.service;

import static com.wanted.safewallet.domain.category.persistence.entity.CategoryType.ETC;
import static com.wanted.safewallet.domain.category.persistence.entity.CategoryType.FOOD;
import static com.wanted.safewallet.domain.category.persistence.entity.CategoryType.TRAFFIC;
import static com.wanted.safewallet.domain.expenditure.web.enums.FinanceStatus.EXCELLENT;
import static com.wanted.safewallet.utils.Fixtures.aCategory;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;

import com.wanted.safewallet.domain.budget.business.service.BudgetService;
import com.wanted.safewallet.domain.category.persistence.entity.Category;
import com.wanted.safewallet.domain.expenditure.business.mapper.ExpenditureMapper;
import com.wanted.safewallet.domain.expenditure.persistence.dto.ExpenditureAmountOfCategoryListDto;
import com.wanted.safewallet.domain.expenditure.persistence.dto.ExpenditureAmountOfCategoryListDto.ExpenditureAmountOfCategoryDto;
import com.wanted.safewallet.domain.expenditure.persistence.repository.ExpenditureRepository;
import com.wanted.safewallet.domain.expenditure.web.dto.response.TodayExpenditureConsultResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ExpenditureConsultServiceTest {

    @InjectMocks
    ExpenditureConsultService expenditureConsultService;

    @Spy
    ExpenditureMapper expenditureMapper;

    @Mock
    BudgetService budgetService;

    @Mock
    ExpenditureRepository expenditureRepository;

    @DisplayName("오늘 지출 추천 서비스 테스트 : 성공")
    @Test
    void consultTodayExpenditure() {
        //given
        String userId = "testUserId";
        Map<Category, Long> budgetAmountByCategory = Map.of(
            aCategory().id(1L).type(FOOD).build(), 300_000L,
            aCategory().id(2L).type(TRAFFIC).build(), 200_000L,
            aCategory().id(3L).type(ETC).build(), 100_000L);
        List<ExpenditureAmountOfCategoryDto> expenditureAmountOfCategoryList = List.of(
            new ExpenditureAmountOfCategoryDto(aCategory().id(1L).type(FOOD).build(), 30_000L),
            new ExpenditureAmountOfCategoryDto(aCategory().id(2L).type(TRAFFIC).build(), 20_000L),
            new ExpenditureAmountOfCategoryDto(aCategory().id(3L).type(ETC).build(), 10_000L));
        ExpenditureAmountOfCategoryListDto expenditureAmountOfCategoryListDto = new ExpenditureAmountOfCategoryListDto(expenditureAmountOfCategoryList);
        given(budgetService.getBudgetAmountByCategory(anyString(), any(YearMonth.class)))
            .willReturn(budgetAmountByCategory);
        given(expenditureRepository.findExpenditureAmountOfCategoryListByUserAndExpenditureDateBetween(
            anyString(), any(LocalDateTime.class), any(LocalDateTime.class)))
            .willReturn(expenditureAmountOfCategoryListDto);

        //LocalDate 내 모든 static 메소드가 아닌 now 메소드만 mocking
        try (MockedStatic<LocalDate> mockedStatic = mockStatic(LocalDate.class, Mockito.CALLS_REAL_METHODS)) {
            LocalDate now = LocalDate.of(2023, 12, 7);
            mockedStatic.when(LocalDate::now).thenReturn(now);

            //when
            TodayExpenditureConsultResponse response = expenditureConsultService.consultTodayExpenditure(userId);

            //then
            then(budgetService).should(times(1))
                .getBudgetAmountByCategory(anyString(), any(YearMonth.class));
            then(expenditureRepository).should(times(1))
                .findExpenditureAmountOfCategoryListByUserAndExpenditureDateBetween(anyString(), any(LocalDateTime.class), any(LocalDateTime.class));
            assertThat(response.getTotalAmount()).isEqualTo(21600);
            assertThat(response.getTotalFinanceStatus()).isEqualTo(EXCELLENT);
            assertThat(response.getTodayExpenditureConsultOfCategoryList()).satisfiesExactly(
                item1 -> assertThat(item1)
                    .extracting("type", "amount", "financeStatus")
                    .containsExactly(FOOD, 10800L, EXCELLENT),
                item2 -> assertThat(item2)
                    .extracting("type", "amount", "financeStatus")
                    .containsExactly(TRAFFIC, 7200L, EXCELLENT),
                item3 -> assertThat(item3)
                    .extracting("type", "amount", "financeStatus")
                    .containsExactly(ETC, 3600L, EXCELLENT));
        }
    }
}
