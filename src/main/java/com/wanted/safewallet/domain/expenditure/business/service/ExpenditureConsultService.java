package com.wanted.safewallet.domain.expenditure.business.service;

import static com.wanted.safewallet.domain.expenditure.web.enums.FinanceStatus.BAD;
import static com.wanted.safewallet.domain.expenditure.web.enums.FinanceStatus.EXCELLENT;
import static com.wanted.safewallet.domain.expenditure.web.enums.FinanceStatus.GOOD;
import static com.wanted.safewallet.domain.expenditure.web.enums.FinanceStatus.WARN;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import com.wanted.safewallet.domain.budget.business.service.BudgetService;
import com.wanted.safewallet.domain.category.persistence.entity.Category;
import com.wanted.safewallet.domain.expenditure.business.vo.TodayExpenditureConsultVo;
import com.wanted.safewallet.domain.expenditure.business.mapper.ExpenditureMapper;
import com.wanted.safewallet.domain.expenditure.persistence.repository.ExpenditureRepository;
import com.wanted.safewallet.domain.expenditure.web.dto.response.TodayExpenditureConsultResponseDto;
import com.wanted.safewallet.domain.expenditure.web.enums.FinanceStatus;
import java.time.LocalDate;
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
        Map<Category, Long> budgetTotalAmountByCategory = budgetService.getBudgetAmountByCategory(userId, currentYearMonth);
        //일별 적정 예산 (카테고리 별)
        Map<Category, Long> budgetAmountPerDayByCategory = getBudgetAmountPerDayByCategory(budgetTotalAmountByCategory, daysOfCurrentMonth);
        //현재 월 내에서 어제까지 총 지출 (카테고리 별)
        Map<Category, Long> expenditureTotalAmountByCategory = expenditureRepository.findTotalAmountMapByUserAndExpenditureDateRange(
            userId, expenditureStartDate, expenditureEndDate);
        //현재 월 내에서 남은 기간 동안 일별 적정 지출 (카테고리 별)
        Map<Category, Long> expenditureAmountPerDayByCategory = getExpenditureAmountPerDayByCategory(
            expenditureTotalAmountByCategory, budgetTotalAmountByCategory, budgetAmountPerDayByCategory, leftDaysOfCurrentMonth);

        long todayTotalAmount = calculateTodayTotalAmount(expenditureAmountPerDayByCategory);
        FinanceStatus totalFinanceStatus = getTotalFinanceStatus(budgetTotalAmountByCategory, budgetAmountPerDayByCategory, expenditureTotalAmountByCategory, expenditureEndDate);
        Map<Category, TodayExpenditureConsultVo> todayExpenditureConsultByCategory =
            getTodayExpenditureConsultByCategory(budgetTotalAmountByCategory, budgetAmountPerDayByCategory, expenditureTotalAmountByCategory, expenditureAmountPerDayByCategory);
        return expenditureMapper.toDto(todayTotalAmount, totalFinanceStatus, todayExpenditureConsultByCategory);
    }

    private Map<Category, Long> getBudgetAmountPerDayByCategory(Map<Category, Long> budgetTotalAmountByCategory, int daysOfCurrentMonth) {
        return budgetTotalAmountByCategory.keySet().stream().collect(toMap(identity(), category ->
            calculateAmountPerDay(budgetTotalAmountByCategory.get(category), daysOfCurrentMonth)));
    }

    private Map<Category, Long> getExpenditureAmountPerDayByCategory(
        Map<Category, Long> expenditureTotalAmountByCategory,
        Map<Category, Long> budgetTotalAmountByCategory, Map<Category, Long> budgetAmountPerDayByCategory,
        int leftDaysOfCurrentMonth) {
        return expenditureTotalAmountByCategory.keySet().stream().collect(toMap(identity(), category ->
            calculateExpenditureAmountPerDay(budgetTotalAmountByCategory.get(category) - expenditureTotalAmountByCategory.get(category),
                budgetAmountPerDayByCategory.get(category), leftDaysOfCurrentMonth)));
    }

    private Long calculateExpenditureAmountPerDay(Long leftBudgetTotalAmount, Long budgetAmountPerDay, int days) {
        long expenditureAmountPerDay = calculateAmountPerDay(leftBudgetTotalAmount, days);
        return expenditureAmountPerDay < 100 ? budgetAmountPerDay : expenditureAmountPerDay;
    }

    private Long calculateAmountPerDay(Long totalAmount, int days) {
        return (long) ((double) totalAmount / days / 100) * 100; //100원 단위로 환산
    }

    private long calculateTodayTotalAmount(Map<Category, Long> expenditureAmountPerDayByCategory) {
        return expenditureAmountPerDayByCategory.values().stream().mapToLong(Long::longValue).sum();
    }

    private FinanceStatus getTotalFinanceStatus(Map<Category, Long> budgetTotalAmountByCategory,
        Map<Category, Long> budgetAmountPerDayByCategory, Map<Category, Long> expenditureTotalAmountByCategory, LocalDate now) {
        long budgetTotalAmount = budgetTotalAmountByCategory.values().stream().mapToLong(Long::longValue).sum();
        long budgetTotalAmountToNow = budgetAmountPerDayByCategory.values().stream().mapToLong(Long::longValue).sum() * now.getDayOfMonth();
        long expenditureTotalAmountToNow = expenditureTotalAmountByCategory.values().stream().mapToLong(Long::longValue).sum();

        if (budgetTotalAmount < expenditureTotalAmountToNow) return BAD; //예산 초과
        Long toleranceAmount = calculateToleranceAmount(budgetTotalAmountToNow);
        if (budgetTotalAmountToNow + toleranceAmount < expenditureTotalAmountToNow) return WARN;
        if (budgetTotalAmountToNow - toleranceAmount <= expenditureTotalAmountToNow &&
            budgetTotalAmountToNow + toleranceAmount >= expenditureTotalAmountToNow) return GOOD;
        return EXCELLENT;
    }

    private Map<Category, TodayExpenditureConsultVo> getTodayExpenditureConsultByCategory(
        Map<Category, Long> budgetTotalAmountByCategory, Map<Category, Long> budgetAmountPerDayByCategory,
        Map<Category, Long> expenditureTotalAmountByCategory, Map<Category, Long> expenditureAmountPerDayByCategory) {
        return expenditureAmountPerDayByCategory.keySet().stream()
            .collect(toMap(identity(), category -> new TodayExpenditureConsultVo(
                expenditureAmountPerDayByCategory.get(category),
                getFinanceStatus(budgetTotalAmountByCategory.get(category),
                    expenditureTotalAmountByCategory.get(category),
                    budgetAmountPerDayByCategory.get(category),
                    expenditureAmountPerDayByCategory.get(category)))));
    }

    private FinanceStatus getFinanceStatus(long budgetTotalAmount, long expenditureTotalAmount,
        long budgetAmountPerDay, long expenditureAmountPerDay) {
        if (budgetTotalAmount < expenditureTotalAmount) return BAD; //예산 초과
        Long toleranceAmount = calculateToleranceAmount(budgetAmountPerDay);
        if (budgetAmountPerDay - toleranceAmount > expenditureAmountPerDay) return WARN;
        if (budgetAmountPerDay - toleranceAmount <= expenditureAmountPerDay &&
            budgetAmountPerDay + toleranceAmount >= expenditureAmountPerDay) return GOOD;
        return EXCELLENT;
    }

    private Long calculateToleranceAmount(long amount) {
        return (long) (amount * 0.1); //TODO: 허용 오차 금액 정확도
    }
}
