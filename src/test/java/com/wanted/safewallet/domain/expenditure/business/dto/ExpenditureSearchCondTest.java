package com.wanted.safewallet.domain.expenditure.business.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ExpenditureSearchCondTest {

    @DisplayName("지출 내역 조회 조건 내 날짜 변환 테스트")
    @Test
    void expenditureSearchCondTest() {
        //given
        LocalDate startDate = LocalDate.of(2023, 12, 1);
        LocalDate endDate = LocalDate.of(2023, 12, 10);

        //when
        ExpenditureSearchCond searchCond = new ExpenditureSearchCond(startDate, endDate,
            List.of(), 1000L, 10000L, List.of());

        //then
        assertThat(searchCond.getStartDate()).isEqualTo(LocalDateTime.of(2023, 12, 1, 0, 0, 0));
        assertThat(searchCond.getEndDate()).isEqualTo(LocalDateTime.of(2023, 12, 11, 0, 0, 0));
    }
}
