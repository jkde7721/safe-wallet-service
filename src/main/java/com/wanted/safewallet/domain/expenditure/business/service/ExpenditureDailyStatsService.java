package com.wanted.safewallet.domain.expenditure.business.service;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import com.wanted.safewallet.domain.category.persistence.entity.Category;
import com.wanted.safewallet.domain.expenditure.business.dto.YesterdayExpenditureTotalDailyStatsDto;
import com.wanted.safewallet.domain.expenditure.business.dto.YesterdayExpenditureDailyStatsDto;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class ExpenditureDailyStatsService {

    public YesterdayExpenditureTotalDailyStatsDto produceYesterdayExpenditureDailyStats(
        Map<Category, Long> dailyConsultedExpenditureAmountByCategory,
        Map<Category, Long> yesterdayExpenditureAmountByCategory) {
        long totalAmount = calculateTotalAmount(yesterdayExpenditureAmountByCategory);
        Map<Category, YesterdayExpenditureDailyStatsDto> yesterdayExpenditureDailyStatsByCategory =
            getYesterdayExpenditureDailyStatsByCategory(dailyConsultedExpenditureAmountByCategory, yesterdayExpenditureAmountByCategory);
        return YesterdayExpenditureTotalDailyStatsDto.builder().totalAmount(totalAmount)
            .yesterdayExpenditureDailyStatsByCategory(yesterdayExpenditureDailyStatsByCategory).build();
    }

    private long calculateTotalAmount(Map<Category, Long> amountByCategory) {
        return amountByCategory.values().stream().mapToLong(Long::longValue).sum();
    }

    private Map<Category, YesterdayExpenditureDailyStatsDto> getYesterdayExpenditureDailyStatsByCategory(
        Map<Category, Long> dailyConsultedExpenditureAmountByCategory, Map<Category, Long> yesterdayExpenditureAmountByCategory) {
        return dailyConsultedExpenditureAmountByCategory.keySet().stream()
            .collect(toMap(identity(), category -> new YesterdayExpenditureDailyStatsDto(
                dailyConsultedExpenditureAmountByCategory.get(category), yesterdayExpenditureAmountByCategory.get(category),
                calculateConsumptionRate(dailyConsultedExpenditureAmountByCategory.get(category),
                    yesterdayExpenditureAmountByCategory.get(category)))));
    }

    private Long calculateConsumptionRate(Long consultedAmount, Long expendedAmount) {
        if (consultedAmount == 0) consultedAmount = 1L;
        return Math.round((double) expendedAmount / consultedAmount * 100); //% 단위로 변환하기 위해 곱하기 100
    }
}
