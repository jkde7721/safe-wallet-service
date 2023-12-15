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
import com.wanted.safewallet.domain.expenditure.persistence.dto.ExpenditureAmountOfCategoryListDto;
import com.wanted.safewallet.domain.expenditure.persistence.dto.ExpenditureAmountOfCategoryListDto.ExpenditureAmountOfCategoryDto;
import com.wanted.safewallet.domain.expenditure.persistence.repository.ExpenditureRepository;
import com.wanted.safewallet.domain.expenditure.web.dto.response.TodayExpenditureConsultResponse;
import com.wanted.safewallet.domain.expenditure.web.dto.response.TodayExpenditureConsultResponse.TodayExpenditureConsultOfCategoryResponse;
import com.wanted.safewallet.domain.expenditure.web.dto.response.YesterdayExpenditureDailyStatsResponse;
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
        List<TodayExpenditureConsultOfCategoryResponse> todayExpenditureConsultOfCategoryList = List.of(
            new TodayExpenditureConsultOfCategoryResponse(1L, FOOD, 9600L, GOOD),
            new TodayExpenditureConsultOfCategoryResponse(2L, TRAFFIC, 6400L, EXCELLENT),
            new TodayExpenditureConsultOfCategoryResponse(3L, ETC, 3200L, GOOD));
        TodayExpenditureConsultResponse todayExpenditureConsult = new TodayExpenditureConsultResponse(
            19200L, GOOD, todayExpenditureConsultOfCategoryList);
        List<ExpenditureAmountOfCategoryDto> expenditureAmountOfCategoryList = List.of(
            new ExpenditureAmountOfCategoryDto(Category.builder().id(1L).type(FOOD).build(), 9600L),
            new ExpenditureAmountOfCategoryDto(Category.builder().id(2L).type(TRAFFIC).build(), 3200L),
            new ExpenditureAmountOfCategoryDto(Category.builder().id(3L).type(ETC).build(), 4800L));
        ExpenditureAmountOfCategoryListDto expenditureAmountOfCategoryListDto = new ExpenditureAmountOfCategoryListDto(expenditureAmountOfCategoryList);
        given(expenditureConsultService.consultTodayExpenditure(anyString())).willReturn(todayExpenditureConsult);
        given(expenditureRepository.findExpenditureAmountOfCategoryListByUserAndExpenditureDateBetween(anyString(), any(LocalDateTime.class), any(LocalDateTime.class)))
            .willReturn(expenditureAmountOfCategoryListDto);

        //when
        YesterdayExpenditureDailyStatsResponse response = expenditureDailyStatsService
            .produceYesterdayExpenditureDailyStats(userId);

        //then
        then(expenditureConsultService).should(times(1))
            .consultTodayExpenditure(anyString());
        then(expenditureRepository).should(times(1))
            .findExpenditureAmountOfCategoryListByUserAndExpenditureDateBetween(anyString(), any(LocalDateTime.class), any(LocalDateTime.class));
        assertThat(response.getTotalAmount()).isEqualTo(17600L);
        assertThat(response.getYesterdayExpenditureDailyStatsOfCategoryList()).satisfiesExactly(
            item1 -> assertThat(item1).extracting("type", "consultedAmount", "expendedAmount", "consumptionRate")
                .containsExactly(FOOD, 9600L, 9600L, 100L),
            item2 -> assertThat(item2).extracting("type", "consultedAmount", "expendedAmount", "consumptionRate")
                .containsExactly(TRAFFIC, 6400L, 3200L, 50L),
            item3 -> assertThat(item3).extracting("type", "consultedAmount", "expendedAmount", "consumptionRate")
                .containsExactly(ETC, 3200L, 4800L, 150L));
    }
}
