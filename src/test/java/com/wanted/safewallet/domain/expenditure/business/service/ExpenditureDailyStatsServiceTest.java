package com.wanted.safewallet.domain.expenditure.business.service;

import static com.wanted.safewallet.domain.category.persistence.entity.CategoryType.ETC;
import static com.wanted.safewallet.domain.category.persistence.entity.CategoryType.FOOD;
import static com.wanted.safewallet.domain.category.persistence.entity.CategoryType.TRAFFIC;
import static com.wanted.safewallet.domain.expenditure.web.enums.FinanceStatus.EXCELLENT;
import static com.wanted.safewallet.domain.expenditure.web.enums.FinanceStatus.GOOD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.wanted.safewallet.domain.category.persistence.entity.Category;
import com.wanted.safewallet.domain.expenditure.business.mapper.ExpenditureMapper;
import com.wanted.safewallet.domain.expenditure.persistence.repository.ExpenditureRepository;
import com.wanted.safewallet.domain.expenditure.web.dto.response.TodayExpenditureConsultResponseDto;
import com.wanted.safewallet.domain.expenditure.web.dto.response.TodayExpenditureConsultResponseDto.TodayExpenditureConsultOfCategoryResponseDto;
import com.wanted.safewallet.domain.expenditure.web.dto.response.TodayExpenditureDailyStatsResponseDto;
import java.time.LocalDate;
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
class ExpenditureDailyStatsServiceTest {

    @InjectMocks
    ExpenditureDailyStatsService expenditureDailyStatsService;

    @Spy
    ExpenditureMapper expenditureMapper;

    @Mock
    ExpenditureConsultService expenditureConsultService;

    @Mock
    ExpenditureRepository expenditureRepository;

    @DisplayName("오늘 지출 안내 서비스 테스트 : 성공")
    @Test
    void produceTodayExpenditureDailyStats() {
        //given
        String userId = "testUserId";
        List<TodayExpenditureConsultOfCategoryResponseDto> todayExpenditureConsultOfCategoryList = List.of(
            new TodayExpenditureConsultOfCategoryResponseDto(1L, FOOD, 9600L, GOOD),
            new TodayExpenditureConsultOfCategoryResponseDto(2L, TRAFFIC, 6400L, EXCELLENT),
            new TodayExpenditureConsultOfCategoryResponseDto(3L, ETC, 3200L, GOOD));
        TodayExpenditureConsultResponseDto todayExpenditureConsult = new TodayExpenditureConsultResponseDto(
            19200L, GOOD, todayExpenditureConsultOfCategoryList);
        Map<Category, Long> todayExpenditureTotalAmountByCategory = Map.of(
            Category.builder().id(1L).type(FOOD).build(), 9600L,
            Category.builder().id(2L).type(TRAFFIC).build(), 3200L,
            Category.builder().id(3L).type(ETC).build(), 4800L);
        given(expenditureConsultService.consultTodayExpenditure(anyString())).willReturn(todayExpenditureConsult);
        given(expenditureRepository.findTotalAmountMapByUserAndExpenditureDate(anyString(), any(LocalDate.class)))
            .willReturn(todayExpenditureTotalAmountByCategory);

        //when
        TodayExpenditureDailyStatsResponseDto responseDto = expenditureDailyStatsService
            .produceTodayExpenditureDailyStats(userId);

        //then
        then(expenditureConsultService).should(times(1))
            .consultTodayExpenditure(anyString());
        then(expenditureRepository).should(times(1))
            .findTotalAmountMapByUserAndExpenditureDate(anyString(), any(LocalDate.class));
        assertThat(responseDto.getTodayTotalAmount()).isEqualTo(17600L);
        assertThat(responseDto.getTodayExpenditureDailyStatsOfCategoryList()).satisfiesExactly(
            item1 -> assertThat(item1).extracting("type", "consultedTotalAmount", "todayTotalAmount", "consumptionRate")
                .containsExactly(FOOD, 9600L, 9600L, 100L),
            item2 -> assertThat(item2).extracting("type", "consultedTotalAmount", "todayTotalAmount", "consumptionRate")
                .containsExactly(TRAFFIC, 6400L, 3200L, 50L),
            item3 -> assertThat(item3).extracting("type", "consultedTotalAmount", "todayTotalAmount", "consumptionRate")
                .containsExactly(ETC, 3200L, 4800L, 150L));
    }
}
