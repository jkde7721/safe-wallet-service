package com.wanted.safewallet.domain.expenditure.business.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.YearMonth;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TodayExpenditureConsultDateDtoTest {

    @DisplayName("오늘 지출 추천 관련 날짜 변환 테스트")
    @Test
    void todayExpenditureConsultDateDtoTest() {
        //given
        LocalDate now = LocalDate.of(2023, 12, 20);

        //when
        TodayExpenditureConsultDateDto todayExpenditureConsultDateDto = new TodayExpenditureConsultDateDto(now);

        //then
        assertThat(todayExpenditureConsultDateDto.getNow()).isEqualTo(LocalDate.of(2023, 12, 20));
        assertThat(todayExpenditureConsultDateDto.getBudgetYearMonth()).isEqualTo(YearMonth.of(2023, 12));
        assertThat(todayExpenditureConsultDateDto.getExpenditureStartDate()).isEqualTo(LocalDate.of(2023, 12, 1));
        assertThat(todayExpenditureConsultDateDto.getExpenditureEndDate()).isEqualTo(LocalDate.of(2023, 12, 19));
        assertThat(todayExpenditureConsultDateDto.getDaysOfCurrentMonth()).isEqualTo(31);
        assertThat(todayExpenditureConsultDateDto.getLeftDaysOfCurrentMonth()).isEqualTo(12);
    }
}
