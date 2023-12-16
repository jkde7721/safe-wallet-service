package com.wanted.safewallet.domain.expenditure.business.service;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import com.wanted.safewallet.domain.category.persistence.entity.Category;
import com.wanted.safewallet.domain.expenditure.business.mapper.ExpenditureMapper;
import com.wanted.safewallet.domain.expenditure.business.dto.YesterdayExpenditureDailyStatsDto;
import com.wanted.safewallet.domain.expenditure.persistence.repository.ExpenditureRepository;
import com.wanted.safewallet.domain.expenditure.web.dto.response.TodayExpenditureConsultResponse;
import com.wanted.safewallet.domain.expenditure.web.dto.response.TodayExpenditureConsultResponse.TodayExpenditureConsultOfCategoryResponse;
import com.wanted.safewallet.domain.expenditure.web.dto.response.YesterdayExpenditureDailyStatsResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ExpenditureDailyStatsService {

    private final ExpenditureMapper expenditureMapper;
    private final ExpenditureConsultService expenditureConsultService;
    private final ExpenditureRepository expenditureRepository;

    public YesterdayExpenditureDailyStatsResponse produceYesterdayExpenditureDailyStats(String userId) {
        LocalDate yesterday = LocalDate.now().minusDays(1); //ex. 10일이 어제라면 현재는 11일
        TodayExpenditureConsultResponse yesterdayExpenditureConsult = expenditureConsultService.consultTodayExpenditure(userId); //캐싱
        Map<Category, Long> dailyConsultedExpenditureAmountByCategory = convertToDailyConsultedExpenditureAmountByCategory(yesterdayExpenditureConsult);
        Map<Category, Long> yesterdayExpenditureAmountByCategory = getExpenditureAmountByCategory(userId, yesterday);

        Long totalAmount = calculateTotalAmount(yesterdayExpenditureAmountByCategory);
        Map<Category, YesterdayExpenditureDailyStatsDto> yesterdayExpenditureDailyStatsByCategory =
            getYesterdayExpenditureDailyStatsByCategory(dailyConsultedExpenditureAmountByCategory, yesterdayExpenditureAmountByCategory);
        return expenditureMapper.toDto(totalAmount, yesterdayExpenditureDailyStatsByCategory);
    }

    private Map<Category, Long> getExpenditureAmountByCategory(String userId, LocalDate date) {
        LocalDateTime startInclusive = date.atStartOfDay();
        LocalDateTime endExclusive = date.plusDays(1).atStartOfDay();
        return expenditureRepository.findExpenditureAmountOfCategoryListByUserAndExpenditureDateBetween(
            userId, startInclusive, endExclusive).toMapByCategory();
    }

    private Map<Category, Long> convertToDailyConsultedExpenditureAmountByCategory(
        TodayExpenditureConsultResponse yesterdayExpenditureConsult) {
        return yesterdayExpenditureConsult.getTodayExpenditureConsultOfCategoryList().stream()
            .collect(toMap(consult -> Category.builder().id(consult.getCategoryId()).type(consult.getType()).build(),
                TodayExpenditureConsultOfCategoryResponse::getAmount));
    }

    private Long calculateTotalAmount(Map<Category, Long> amountByCategory) {
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
