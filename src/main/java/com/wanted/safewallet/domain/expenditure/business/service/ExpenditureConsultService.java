package com.wanted.safewallet.domain.expenditure.business.service;

import static com.wanted.safewallet.domain.expenditure.web.enums.FinanceStatus.BAD;
import static com.wanted.safewallet.domain.expenditure.web.enums.FinanceStatus.EXCELLENT;
import static com.wanted.safewallet.domain.expenditure.web.enums.FinanceStatus.GOOD;
import static com.wanted.safewallet.domain.expenditure.web.enums.FinanceStatus.WARN;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import com.wanted.safewallet.domain.budget.business.service.BudgetService;
import com.wanted.safewallet.domain.category.persistence.entity.Category;
import com.wanted.safewallet.domain.expenditure.business.dto.TodayExpenditureConsultDto;
import com.wanted.safewallet.domain.expenditure.business.mapper.ExpenditureMapper;
import com.wanted.safewallet.domain.expenditure.persistence.repository.ExpenditureRepository;
import com.wanted.safewallet.domain.expenditure.web.dto.response.TodayExpenditureConsultResponseDto;
import com.wanted.safewallet.domain.expenditure.web.enums.FinanceStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ExpenditureConsultService {

    private final ExpenditureMapper expenditureMapper;
    private final BudgetService budgetService;
    private final ExpenditureRepository expenditureRepository;
    public static final String CACHE_NAME = "today-expenditure-consult";

    @Cacheable(cacheNames = CACHE_NAME, key = "#userId")
    public TodayExpenditureConsultResponseDto consultTodayExpenditure(String userId) {
        LocalDate expenditureEndDate = LocalDate.now();
        YearMonth currentYearMonth = YearMonth.of(expenditureEndDate.getYear(), expenditureEndDate.getMonth());
        LocalDate expenditureStartDate = currentYearMonth.atDay(1);
        int daysOfCurrentMonth = currentYearMonth.lengthOfMonth();
        int leftDaysOfCurrentMonth = daysOfCurrentMonth - expenditureEndDate.getDayOfMonth() + 1;
        //월별 총 예산 (카테고리 별)
        Map<Category, Long> monthlyBudgetAmountByCategory = budgetService.getBudgetAmountByCategory(userId, currentYearMonth);
        //일별 적정 예산 (카테고리 별)
        Map<Category, Long> dailyBudgetAmountByCategory = getDailyBudgetAmountByCategory(monthlyBudgetAmountByCategory, daysOfCurrentMonth);
        //현재 월 내에서 어제까지 총 지출 (카테고리 별)
        Map<Category, Long> monthlyExpendedExpenditureAmountByCategory = getExpenditureAmountByCategory(userId, expenditureStartDate, expenditureEndDate);
        //현재 월 내에서 남은 기간 동안 일별 적정 지출 (카테고리 별)
        Map<Category, Long> dailyConsultedExpenditureAmountByCategory = getDailyConsultedExpenditureAmountByCategory(
            monthlyExpendedExpenditureAmountByCategory, monthlyBudgetAmountByCategory, dailyBudgetAmountByCategory, leftDaysOfCurrentMonth);

        long totalAmount = calculateTotalAmount(dailyConsultedExpenditureAmountByCategory);
        FinanceStatus totalFinanceStatus = getTotalFinanceStatus(monthlyBudgetAmountByCategory, dailyBudgetAmountByCategory, monthlyExpendedExpenditureAmountByCategory, expenditureEndDate);
        Map<Category, TodayExpenditureConsultDto> todayExpenditureConsultByCategory =
            getTodayExpenditureConsultByCategory(monthlyBudgetAmountByCategory, dailyBudgetAmountByCategory, monthlyExpendedExpenditureAmountByCategory, dailyConsultedExpenditureAmountByCategory);
        return expenditureMapper.toDto(totalAmount, totalFinanceStatus, todayExpenditureConsultByCategory);
    }

    private Map<Category, Long> getExpenditureAmountByCategory(String userId, LocalDate startDate, LocalDate endDate) {
        LocalDateTime startInclusive = startDate.atStartOfDay();
        LocalDateTime endExclusive = endDate.plusDays(1).atStartOfDay();
        return expenditureRepository.findExpenditureAmountOfCategoryListByUserAndExpenditureDateBetween(
            userId, startInclusive, endExclusive).toMapByCategory();
    }

    private Map<Category, Long> getDailyBudgetAmountByCategory(Map<Category, Long> monthlyBudgetAmountByCategory, int daysOfCurrentMonth) {
        return monthlyBudgetAmountByCategory.keySet().stream().collect(toMap(identity(), category ->
            calculateDailyAmount(monthlyBudgetAmountByCategory.get(category), daysOfCurrentMonth)));
    }

    private Map<Category, Long> getDailyConsultedExpenditureAmountByCategory(
        Map<Category, Long> monthlyExpendedExpenditureAmountByCategory,
        Map<Category, Long> monthlyBudgetAmountByCategory, Map<Category, Long> dailyBudgetAmountByCategory, int leftDaysOfCurrentMonth) {
        return monthlyExpendedExpenditureAmountByCategory.keySet().stream().collect(toMap(identity(), category ->
            calculateDailyConsultedExpenditureAmount(monthlyBudgetAmountByCategory.get(category) - monthlyExpendedExpenditureAmountByCategory.get(category),
                dailyBudgetAmountByCategory.get(category), leftDaysOfCurrentMonth)));
    }

    private Long calculateDailyConsultedExpenditureAmount(Long leftBudgetAmount, Long dailyBudgetAmount, int days) {
        long dailyConsultedExpenditureAmount = calculateDailyAmount(leftBudgetAmount, days);
        return dailyConsultedExpenditureAmount < 100 ? dailyBudgetAmount : dailyConsultedExpenditureAmount;
    }

    private Long calculateDailyAmount(Long totalAmount, int days) {
        return (long) ((double) totalAmount / days / 100) * 100; //100원 단위로 환산
    }

    private long calculateTotalAmount(Map<Category, Long> amountByCategory) {
        return amountByCategory.values().stream().mapToLong(Long::longValue).sum();
    }

    private FinanceStatus getTotalFinanceStatus(Map<Category, Long> monthlyBudgetAmountByCategory,
        Map<Category, Long> dailyBudgetAmountByCategory, Map<Category, Long> monthlyExpendedExpenditureAmountByCategory, LocalDate now) {
        long monthlyBudgetTotalAmount = calculateTotalAmount(monthlyBudgetAmountByCategory);
        long dailyBudgetTotalAmountToYesterday = calculateTotalAmount(dailyBudgetAmountByCategory) * (now.getDayOfMonth() - 1);
        long monthlyExpendedExpenditureTotalAmount = calculateTotalAmount(monthlyExpendedExpenditureAmountByCategory);

        if (monthlyBudgetTotalAmount < monthlyExpendedExpenditureTotalAmount) return BAD; //예산 초과
        Long toleranceAmount = calculateToleranceAmount(dailyBudgetTotalAmountToYesterday);
        if (dailyBudgetTotalAmountToYesterday + toleranceAmount < monthlyExpendedExpenditureTotalAmount) return WARN;
        if (dailyBudgetTotalAmountToYesterday - toleranceAmount <= monthlyExpendedExpenditureTotalAmount &&
            dailyBudgetTotalAmountToYesterday + toleranceAmount >= monthlyExpendedExpenditureTotalAmount) return GOOD;
        return EXCELLENT;
    }

    private Map<Category, TodayExpenditureConsultDto> getTodayExpenditureConsultByCategory(
        Map<Category, Long> monthlyBudgetAmountByCategory, Map<Category, Long> dailyBudgetAmountByCategory,
        Map<Category, Long> monthlyExpendedExpenditureAmountByCategory, Map<Category, Long> dailyConsultedExpenditureAmountByCategory) {
        return dailyConsultedExpenditureAmountByCategory.keySet().stream()
            .collect(toMap(identity(), category -> new TodayExpenditureConsultDto(
                dailyConsultedExpenditureAmountByCategory.get(category),
                getFinanceStatus(monthlyBudgetAmountByCategory.get(category),
                    monthlyExpendedExpenditureAmountByCategory.get(category),
                    dailyBudgetAmountByCategory.get(category),
                    dailyConsultedExpenditureAmountByCategory.get(category)))));
    }

    private FinanceStatus getFinanceStatus(long monthlyBudgetAmount, long monthlyExpendedExpenditureAmount,
        long dailyBudgetAmount, long dailyConsultedExpenditureAmount) {
        if (monthlyBudgetAmount < monthlyExpendedExpenditureAmount) return BAD; //예산 초과
        Long toleranceAmount = calculateToleranceAmount(dailyBudgetAmount);
        if (dailyBudgetAmount - toleranceAmount > dailyConsultedExpenditureAmount) return WARN;
        if (dailyBudgetAmount - toleranceAmount <= dailyConsultedExpenditureAmount &&
            dailyBudgetAmount + toleranceAmount >= dailyConsultedExpenditureAmount) return GOOD;
        return EXCELLENT;
    }

    private Long calculateToleranceAmount(long amount) {
        return (long) (amount * 0.1); //TODO: 허용 오차 금액 정확도
    }
}
