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
import com.wanted.safewallet.domain.expenditure.persistence.dto.response.ExpenditureAmountOfCategoryListResponseDto;
import com.wanted.safewallet.domain.expenditure.persistence.dto.response.ExpenditureAmountOfCategoryListResponseDto.ExpenditureAmountOfCategoryResponseDto;
import com.wanted.safewallet.domain.expenditure.persistence.repository.ExpenditureRepository;
import com.wanted.safewallet.domain.expenditure.web.dto.response.TodayExpenditureConsultResponseDto;
import com.wanted.safewallet.domain.expenditure.web.dto.response.TodayExpenditureConsultResponseDto.TodayExpenditureConsultOfCategoryResponseDto;
import com.wanted.safewallet.domain.expenditure.web.dto.response.YesterdayExpenditureDailyStatsResponseDto;
import java.time.LocalDateTime;
import java.util.List;
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

    @DisplayName("어제 지출 안내 서비스 테스트 : 성공")
    @Test
    void produceYesterdayExpenditureDailyStats() {
        //given
        String userId = "testUserId";
        List<TodayExpenditureConsultOfCategoryResponseDto> todayExpenditureConsultOfCategoryList = List.of(
            new TodayExpenditureConsultOfCategoryResponseDto(1L, FOOD, 9600L, GOOD),
            new TodayExpenditureConsultOfCategoryResponseDto(2L, TRAFFIC, 6400L, EXCELLENT),
            new TodayExpenditureConsultOfCategoryResponseDto(3L, ETC, 3200L, GOOD));
        TodayExpenditureConsultResponseDto todayExpenditureConsult = new TodayExpenditureConsultResponseDto(
            19200L, GOOD, todayExpenditureConsultOfCategoryList);
        List<ExpenditureAmountOfCategoryResponseDto> expenditureAmountOfCategoryList = List.of(
            new ExpenditureAmountOfCategoryResponseDto(Category.builder().id(1L).type(FOOD).build(), 9600L),
            new ExpenditureAmountOfCategoryResponseDto(Category.builder().id(2L).type(TRAFFIC).build(), 3200L),
            new ExpenditureAmountOfCategoryResponseDto(Category.builder().id(3L).type(ETC).build(), 4800L));
        ExpenditureAmountOfCategoryListResponseDto expenditureAmountOfCategoryListDto = new ExpenditureAmountOfCategoryListResponseDto(expenditureAmountOfCategoryList);
        given(expenditureConsultService.consultTodayExpenditure(anyString())).willReturn(todayExpenditureConsult);
        given(expenditureRepository.findExpenditureAmountOfCategoryListByUserAndExpenditureDateBetween(anyString(), any(LocalDateTime.class), any(LocalDateTime.class)))
            .willReturn(expenditureAmountOfCategoryListDto);

        //when
        YesterdayExpenditureDailyStatsResponseDto responseDto = expenditureDailyStatsService
            .produceYesterdayExpenditureDailyStats(userId);

        //then
        then(expenditureConsultService).should(times(1))
            .consultTodayExpenditure(anyString());
        then(expenditureRepository).should(times(1))
            .findExpenditureAmountOfCategoryListByUserAndExpenditureDateBetween(anyString(), any(LocalDateTime.class), any(LocalDateTime.class));
        assertThat(responseDto.getTotalAmount()).isEqualTo(17600L);
        assertThat(responseDto.getYesterdayExpenditureDailyStatsOfCategoryList()).satisfiesExactly(
            item1 -> assertThat(item1).extracting("type", "consultedAmount", "expendedAmount", "consumptionRate")
                .containsExactly(FOOD, 9600L, 9600L, 100L),
            item2 -> assertThat(item2).extracting("type", "consultedAmount", "expendedAmount", "consumptionRate")
                .containsExactly(TRAFFIC, 6400L, 3200L, 50L),
            item3 -> assertThat(item3).extracting("type", "consultedAmount", "expendedAmount", "consumptionRate")
                .containsExactly(ETC, 3200L, 4800L, 150L));
    }
}
