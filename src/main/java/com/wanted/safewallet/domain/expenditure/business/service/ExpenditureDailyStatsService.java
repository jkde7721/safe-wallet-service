package com.wanted.safewallet.domain.expenditure.business.service;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import com.wanted.safewallet.domain.category.persistence.entity.Category;
import com.wanted.safewallet.domain.expenditure.business.mapper.ExpenditureMapper;
import com.wanted.safewallet.domain.expenditure.business.vo.TodayExpenditureDailyStatsVo;
import com.wanted.safewallet.domain.expenditure.persistence.repository.ExpenditureRepository;
import com.wanted.safewallet.domain.expenditure.web.dto.response.TodayExpenditureConsultResponseDto;
import com.wanted.safewallet.domain.expenditure.web.dto.response.TodayExpenditureConsultResponseDto.TodayExpenditureConsultOfCategoryResponseDto;
import com.wanted.safewallet.domain.expenditure.web.dto.response.TodayExpenditureDailyStatsResponseDto;
import java.time.LocalDate;
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

    public TodayExpenditureDailyStatsResponseDto produceTodayExpenditureDailyStats(String userId) {
        LocalDate now = LocalDate.now().minusDays(1); //오늘 지출 안내는 다음날 계산하므로 -1
        TodayExpenditureConsultResponseDto todayExpenditureConsult = expenditureConsultService.consultTodayExpenditure(userId);
        Map<Category, Long> budgetAmountPerDayByCategory = convertToBudgetAmountPerDayByCategory(todayExpenditureConsult);
        Map<Category, Long> todayExpenditureTotalAmountByCategory = expenditureRepository.findTotalAmountMapByUserAndExpenditureDate(userId, now);

        Long todayTotalAmount = calculateTodayTotalAmount(todayExpenditureTotalAmountByCategory);
        Map<Category, TodayExpenditureDailyStatsVo> todayExpenditureDailyStatsByCategory =
            getTodayExpenditureDailyStatsByCategory(budgetAmountPerDayByCategory, todayExpenditureTotalAmountByCategory);
        return expenditureMapper.toDto(todayTotalAmount, todayExpenditureDailyStatsByCategory);
    }

    private Map<Category, Long> convertToBudgetAmountPerDayByCategory(TodayExpenditureConsultResponseDto todayExpenditureConsult) {
        return todayExpenditureConsult.getTodayExpenditureConsultOfCategoryList().stream()
            .collect(toMap(consult -> Category.builder().id(consult.getCategoryId()).type(consult.getType()).build(),
                TodayExpenditureConsultOfCategoryResponseDto::getTodayTotalAmount));
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
