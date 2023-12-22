package com.wanted.safewallet.domain.expenditure.business.service;

import static com.wanted.safewallet.domain.category.persistence.entity.CategoryType.ETC;
import static com.wanted.safewallet.domain.category.persistence.entity.CategoryType.FOOD;
import static com.wanted.safewallet.domain.category.persistence.entity.CategoryType.TRAFFIC;
import static com.wanted.safewallet.domain.expenditure.business.enums.FinanceStatus.EXCELLENT;
import static com.wanted.safewallet.domain.expenditure.business.enums.FinanceStatus.GOOD;
import static com.wanted.safewallet.utils.Fixtures.aCategory;
import static org.assertj.core.api.Assertions.assertThat;

import com.wanted.safewallet.domain.category.persistence.entity.Category;
import com.wanted.safewallet.domain.expenditure.business.dto.TodayExpenditureConsultDateDto;
import com.wanted.safewallet.domain.expenditure.business.dto.TodayExpenditureConsultDto;
import com.wanted.safewallet.domain.expenditure.business.dto.TodayExpenditureTotalConsultDto;
import java.time.LocalDate;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ExpenditureConsultServiceTest {

    @InjectMocks
    ExpenditureConsultService expenditureConsultService;

    @DisplayName("오늘 지출 추천 서비스 테스트 : 성공")
    @Test
    void consultTodayExpenditure() {
        //given
        TodayExpenditureConsultDateDto todayExpenditureConsultDateDto = new TodayExpenditureConsultDateDto(
            LocalDate.of(2023, 12, 2));
        Map<Category, Long> monthlyBudgetAmountByCategory = Map.of(
            aCategory().id(1L).type(FOOD).build(), 300_000L,
            aCategory().id(2L).type(TRAFFIC).build(), 200_000L,
            aCategory().id(3L).type(ETC).build(), 100_000L);
        Map<Category, Long> monthlyExpendedExpenditureAmountByCategory = Map.of(
            aCategory().id(1L).type(FOOD).build(), 11_000L,
            aCategory().id(2L).type(TRAFFIC).build(), 5000L,
            aCategory().id(3L).type(ETC).build(), 0L);

        //when
        TodayExpenditureTotalConsultDto consultDto = expenditureConsultService.consultTodayExpenditure(
            todayExpenditureConsultDateDto, monthlyBudgetAmountByCategory, monthlyExpendedExpenditureAmountByCategory);

        //then
        assertThat(consultDto.getTotalAmount()).isEqualTo(19400);
        assertThat(consultDto.getTotalFinanceStatus()).isEqualTo(EXCELLENT);
        assertThat(consultDto.getTodayExpenditureConsultByCategory()).containsExactlyInAnyOrderEntriesOf(Map.of(
            aCategory().type(FOOD).build(), new TodayExpenditureConsultDto(9600L, GOOD),
            aCategory().type(TRAFFIC).build(), new TodayExpenditureConsultDto(6500L, GOOD),
            aCategory().type(ETC).build(), new TodayExpenditureConsultDto(3300L, GOOD)));
    }
}
