package com.wanted.safewallet.domain.expenditure.business.dto;

import static com.wanted.safewallet.domain.expenditure.web.enums.StatsCriteria.LAST_MONTH;
import static com.wanted.safewallet.domain.expenditure.web.enums.StatsCriteria.LAST_WEEK;
import static com.wanted.safewallet.domain.expenditure.web.enums.StatsCriteria.LAST_YEAR;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.MONTHS;
import static java.time.temporal.ChronoUnit.WEEKS;
import static java.time.temporal.ChronoUnit.YEARS;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ExpenditureStatsDateDtoTest {

    @DisplayName("지출 통계 관련 날짜 변환 테스트 - 지난 년도")
    @Test
    void expenditureStatsDateDtoTest_lastYear() {
        //given
        LocalDate now = LocalDate.of(2023, 12, 20);

        //when
        ExpenditureStatsDateDto expenditureStatsDateDto = new ExpenditureStatsDateDto(now, LAST_YEAR);

        //then
        assertThat(expenditureStatsDateDto.getCriteriaStartDate()).isEqualTo(LocalDate.of(2022, 1, 1));
        assertThat(YEARS.between(expenditureStatsDateDto.getCriteriaStartDate(), expenditureStatsDateDto.getCurrentStartDate()))
            .isEqualTo(1);
        assertThat(DAYS.between(expenditureStatsDateDto.getCurrentStartDate(), expenditureStatsDateDto.getCurrentEndDate()))
            .isEqualTo(DAYS.between(expenditureStatsDateDto.getCriteriaStartDate(), expenditureStatsDateDto.getCriteriaEndDate()));
    }

    @DisplayName("지출 통계 관련 날짜 변환 테스트 - 지난 달")
    @Test
    void expenditureStatsDateDtoTest_lastMonth() {
        //given
        LocalDate now = LocalDate.of(2023, 12, 20);

        //when
        ExpenditureStatsDateDto expenditureStatsDateDto = new ExpenditureStatsDateDto(now, LAST_MONTH);

        //then
        assertThat(expenditureStatsDateDto.getCriteriaStartDate()).isEqualTo(LocalDate.of(2023, 11, 1));
        assertThat(MONTHS.between(expenditureStatsDateDto.getCriteriaStartDate(), expenditureStatsDateDto.getCurrentStartDate()))
            .isEqualTo(1);
        assertThat(DAYS.between(expenditureStatsDateDto.getCurrentStartDate(), expenditureStatsDateDto.getCurrentEndDate()))
            .isEqualTo(DAYS.between(expenditureStatsDateDto.getCriteriaStartDate(), expenditureStatsDateDto.getCriteriaEndDate()));
    }

    @DisplayName("지출 통계 관련 날짜 변환 테스트 - 지난 주")
    @Test
    void expenditureStatsDateDtoTest_lastWeek() {
        //given
        LocalDate now = LocalDate.of(2023, 12, 20);

        //when
        ExpenditureStatsDateDto expenditureStatsDateDto = new ExpenditureStatsDateDto(now, LAST_WEEK);

        //then
        assertThat(WEEKS.between(expenditureStatsDateDto.getCriteriaStartDate(), expenditureStatsDateDto.getCurrentStartDate()))
            .isEqualTo(1);
        assertThat(DAYS.between(expenditureStatsDateDto.getCurrentStartDate(), expenditureStatsDateDto.getCurrentEndDate()))
            .isEqualTo(DAYS.between(expenditureStatsDateDto.getCriteriaStartDate(), expenditureStatsDateDto.getCriteriaEndDate()));
    }
}
