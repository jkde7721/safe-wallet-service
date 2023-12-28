package com.wanted.safewallet.domain.expenditure.business.service;

import static com.wanted.safewallet.domain.category.persistence.entity.CategoryType.ETC;
import static com.wanted.safewallet.domain.category.persistence.entity.CategoryType.FOOD;
import static com.wanted.safewallet.domain.category.persistence.entity.CategoryType.TRAFFIC;
import static com.wanted.safewallet.utils.Fixtures.aCategory;
import static org.assertj.core.api.Assertions.assertThat;

import com.wanted.safewallet.domain.category.persistence.entity.Category;
import com.wanted.safewallet.domain.expenditure.business.dto.YesterdayExpenditureDailyStatsDto;
import com.wanted.safewallet.domain.expenditure.business.dto.YesterdayExpenditureTotalDailyStatsDto;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ExpenditureDailyStatsServiceTest {

    @InjectMocks
    ExpenditureDailyStatsService expenditureDailyStatsService;

    @DisplayName("어제 지출 안내 서비스 테스트 : 성공")
    @Test
    void produceYesterdayExpenditureDailyStats() {
        //given
        Map<Category, Long> dailyConsultedExpenditureAmountByCategory = Map.of(
            aCategory().id(1L).type(FOOD).build(), 9600L,
            aCategory().id(2L).type(TRAFFIC).build(), 6400L,
            aCategory().id(3L).type(ETC).build(), 3200L);
        Map<Category, Long> yesterdayExpenditureAmountByCategory = Map.of(
            aCategory().id(1L).type(FOOD).build(), 9600L,
            aCategory().id(2L).type(TRAFFIC).build(), 3200L,
            aCategory().id(3L).type(ETC).build(), 4800L);

        //when
        YesterdayExpenditureTotalDailyStatsDto dailyStatsDto = expenditureDailyStatsService.produceYesterdayExpenditureDailyStats(
            dailyConsultedExpenditureAmountByCategory, yesterdayExpenditureAmountByCategory);

        //then
        assertThat(dailyStatsDto.getTotalAmount()).isEqualTo(17600L);
        assertThat(dailyStatsDto.getYesterdayExpenditureDailyStatsByCategory())
            .containsExactlyInAnyOrderEntriesOf(Map.of(
                aCategory().type(FOOD).build(), new YesterdayExpenditureDailyStatsDto(9600L, 9600L, 100L),
                aCategory().type(TRAFFIC).build(), new YesterdayExpenditureDailyStatsDto(6400L, 3200L, 50L),
                aCategory().type(ETC).build(), new YesterdayExpenditureDailyStatsDto(3200L, 4800L, 150L)));
    }
}
