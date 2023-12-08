package com.wanted.safewallet.domain.expenditure.business.service;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import com.wanted.safewallet.domain.budget.business.service.BudgetService;
import com.wanted.safewallet.domain.category.persistence.entity.Category;
import com.wanted.safewallet.domain.expenditure.business.mapper.ExpenditureMapper;
import com.wanted.safewallet.domain.expenditure.business.vo.TodayExpenditureDailyStatsVo;
import com.wanted.safewallet.domain.expenditure.persistence.repository.ExpenditureRepository;
import com.wanted.safewallet.domain.expenditure.web.dto.response.TodayExpenditureDailyStatsResponseDto;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ExpenditureDailyStatsService {

    private final ExpenditureMapper expenditureMapper;
    private final BudgetService budgetService;
    private final ExpenditureRepository expenditureRepository;

    public TodayExpenditureDailyStatsResponseDto produceTodayExpenditureDailyStats(String userId) {
        LocalDate expenditureDate = LocalDate.now();
        YearMonth currentYearMonth = YearMonth.of(expenditureDate.getYear(), expenditureDate.getMonth());
        int daysOfCurrentMonth = currentYearMonth.lengthOfMonth();
        Map<Category, Long> budgetTotalAmountByCategory = budgetService.getBudgetTotalAmountByCategory(userId, currentYearMonth);
        //TODO: 아래 값 정확하지 않음 (현재까지의 지출 고려X)
        Map<Category, Long> budgetAmountPerDayByCategory = getBudgetAmountPerDayByCategory(budgetTotalAmountByCategory, daysOfCurrentMonth);
        Map<Category, Long> todayExpenditureTotalAmountByCategory = expenditureRepository.findTotalAmountMapByUserAndExpenditureDate(userId, expenditureDate);

        Long todayTotalAmount = calculateTodayTotalAmount(todayExpenditureTotalAmountByCategory);
        Map<Category, TodayExpenditureDailyStatsVo> todayExpenditureDailyStatsByCategory =
            getTodayExpenditureDailyStatsByCategory(budgetAmountPerDayByCategory, todayExpenditureTotalAmountByCategory);
        return expenditureMapper.toDto(todayTotalAmount, todayExpenditureDailyStatsByCategory);
    }

    //추후 캐시 적용으로 제거할 메소드
    private Map<Category, Long> getBudgetAmountPerDayByCategory(Map<Category, Long> budgetTotalAmountByCategory, int daysOfCurrentMonth) {
        return budgetTotalAmountByCategory.keySet().stream().collect(toMap(identity(), category ->
            calculateAmountPerDay(budgetTotalAmountByCategory.get(category), daysOfCurrentMonth)));
    }

    //추후 캐시 적용으로 제거할 메소드
    private Long calculateAmountPerDay(Long totalAmount, int days) {
        return (long) ((double) totalAmount / days / 100) * 100; //100원 단위로 환산
    }

    private Long calculateTodayTotalAmount(Map<Category, Long> todayExpenditureTotalAmountByCategory) {
        return todayExpenditureTotalAmountByCategory.values().stream().mapToLong(Long::longValue).sum();
    }

    private Map<Category, TodayExpenditureDailyStatsVo> getTodayExpenditureDailyStatsByCategory(
        Map<Category, Long> budgetAmountPerDayByCategory, Map<Category, Long> todayExpenditureTotalAmountByCategory) {
        return budgetAmountPerDayByCategory.keySet().stream()
            .collect(toMap(identity(), category -> new TodayExpenditureDailyStatsVo(
                budgetAmountPerDayByCategory.get(category), todayExpenditureTotalAmountByCategory.get(category),
                calculateConsumptionRate(budgetAmountPerDayByCategory.get(category),
                    todayExpenditureTotalAmountByCategory.get(category)))));
    }

    //TODO: ExpenditureService 내 메소드와 중복되므로 제거
    private Long calculateConsumptionRate(Long budgetAmount, Long expenditureAmount) {
        if (budgetAmount == 0) budgetAmount = 1L;
        return Math.round((double) expenditureAmount / budgetAmount * 100); //% 단위로 변환하기 위해 곱하기 100
    }
}
