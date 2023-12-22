package com.wanted.safewallet.domain.expenditure.business.service;

import static com.wanted.safewallet.global.exception.ErrorCode.FORBIDDEN_EXPENDITURE;
import static com.wanted.safewallet.global.exception.ErrorCode.NOT_FOUND_EXPENDITURE;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import com.wanted.safewallet.domain.category.persistence.entity.Category;
import com.wanted.safewallet.domain.expenditure.business.dto.ExpenditureSearchCond;
import com.wanted.safewallet.domain.expenditure.business.dto.ExpenditureSearchDto;
import com.wanted.safewallet.domain.expenditure.business.dto.ExpenditureSearchExceptsDto;
import com.wanted.safewallet.domain.expenditure.business.dto.ExpenditureStatsDateDto;
import com.wanted.safewallet.domain.expenditure.business.dto.ExpenditureStatsDto;
import com.wanted.safewallet.domain.expenditure.business.dto.ExpenditureUpdateDto;
import com.wanted.safewallet.domain.expenditure.persistence.entity.Expenditure;
import com.wanted.safewallet.domain.expenditure.persistence.repository.ExpenditureRepository;
import com.wanted.safewallet.global.exception.BusinessException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ExpenditureService {

    private final ExpenditureRepository expenditureRepository;

    public ExpenditureSearchDto searchExpenditure(String userId, ExpenditureSearchCond searchCond,
        Pageable pageable) {
        long totalAmount = expenditureRepository.findTotalAmountByUserAndSearchCond(userId, searchCond);
        Map<Category, Long> expenditureAmountByCategory = getExpenditureAmountByCategory(userId, searchCond);
        Page<Expenditure> expenditurePage = expenditureRepository.findAllByUserAndSearchCondFetch(userId, searchCond, pageable);
        return ExpenditureSearchDto.builder().totalAmount(totalAmount)
            .expenditureAmountByCategory(expenditureAmountByCategory)
            .expenditurePage(expenditurePage).build();
    }

    public ExpenditureSearchExceptsDto searchExpenditureExcepts(String userId,
        ExpenditureSearchCond searchCond) {
        long totalAmount = expenditureRepository.findTotalAmountByUserAndSearchCond(userId, searchCond);
        Map<Category, Long> expenditureAmountByCategory = getExpenditureAmountByCategory(userId, searchCond);
        return ExpenditureSearchExceptsDto.builder().totalAmount(totalAmount)
            .expenditureAmountByCategory(expenditureAmountByCategory).build();
    }

    @Transactional
    public Expenditure saveExpenditure(Expenditure expenditure) {
        return expenditureRepository.save(expenditure);
    }

    @Transactional
    public void updateExpenditure(Expenditure expenditure, ExpenditureUpdateDto updateDto) {
        expenditure.update(updateDto.getCategoryId(), updateDto.getType(), updateDto.getExpenditureDate(),
            updateDto.getAmount(), updateDto.getTitle(), updateDto.getNote());
    }

    @Transactional
    public void deleteExpenditure(Expenditure expenditure) {
        expenditure.softDelete();
    }

    public ExpenditureStatsDto produceExpenditureStats(String userId, ExpenditureStatsDateDto expenditureStatsDateDto) {
        Map<Category, Long> currentExpenditureAmountByCategory = getExpenditureAmountByCategory(userId,
            expenditureStatsDateDto.getCurrentStartDate(), expenditureStatsDateDto.getCurrentEndDate());
        Map<Category, Long> criteriaExpenditureAmountByCategory = getExpenditureAmountByCategory(userId,
            expenditureStatsDateDto.getCriteriaStartDate(), expenditureStatsDateDto.getCriteriaEndDate());
        Long totalConsumptionRate = calculateTotalConsumptionRate(
            currentExpenditureAmountByCategory, criteriaExpenditureAmountByCategory);
        Map<Category, Long> consumptionRateByCategory = calculateConsumptionRateByCategory(
            currentExpenditureAmountByCategory, criteriaExpenditureAmountByCategory);
        return ExpenditureStatsDto.builder()
            .totalConsumptionRate(totalConsumptionRate)
            .consumptionRateByCategory(consumptionRateByCategory).build();
    }

    public Map<Category, Long> getExpenditureAmountByCategory(String userId, LocalDate date) {
        LocalDateTime startInclusive = date.atStartOfDay();
        LocalDateTime endExclusive = date.plusDays(1).atStartOfDay();
        return expenditureRepository.findExpenditureAmountOfCategoryListByUserAndExpenditureDateBetween(
            userId, startInclusive, endExclusive).toMapByCategory();
    }

    public Map<Category, Long> getExpenditureAmountByCategory(String userId, LocalDate startInclusive, LocalDate endInclusive) {
        LocalDateTime endExclusive = endInclusive.plusDays(1).atStartOfDay();
        return expenditureRepository.findExpenditureAmountOfCategoryListByUserAndExpenditureDateBetween(
            userId, startInclusive.atStartOfDay(), endExclusive).toMapByCategory();
    }

    public Map<Category, Long> getExpenditureAmountByCategory(String userId, ExpenditureSearchCond searchCond) {
        return expenditureRepository.findExpenditureAmountOfCategoryListByUserAndSearchCond(
            userId, searchCond).toMapByCategory();
    }

    public Expenditure getValidExpenditure(String userId, Long expenditureId) {
        Expenditure expenditure = getExpenditure(expenditureId);
        if (!Objects.equals(expenditure.getUser().getId(), userId)) {
            throw new BusinessException(FORBIDDEN_EXPENDITURE);
        }
        return expenditure;
    }

    public Expenditure getValidExpenditureWithCategoryAndImages(String userId, Long expenditureId) {
        Expenditure expenditure = getExpenditureWithCategoryAndImages(expenditureId);
        if (!Objects.equals(expenditure.getUser().getId(), userId)) {
            throw new BusinessException(FORBIDDEN_EXPENDITURE);
        }
        return expenditure;
    }

    public Expenditure getExpenditure(Long expenditureId) {
        return expenditureRepository.findById(expenditureId)
            .orElseThrow(() -> new BusinessException(NOT_FOUND_EXPENDITURE));
    }

    public Expenditure getExpenditureWithCategoryAndImages(Long expenditureId) {
        return expenditureRepository.findByIdFetch(expenditureId)
            .orElseThrow(() -> new BusinessException(NOT_FOUND_EXPENDITURE));
    }

    private Long calculateTotalConsumptionRate(
        Map<Category, Long> currentExpenditureAmountByCategory,
        Map<Category, Long> criteriaExpenditureAmountByCategory) {
        long currentTotalAmount = calculateTotalAmount(currentExpenditureAmountByCategory);
        long criteriaTotalAmount = calculateTotalAmount(criteriaExpenditureAmountByCategory);
        return calculateConsumptionRate(currentTotalAmount, criteriaTotalAmount);
    }

    private long calculateTotalAmount(Map<Category, Long> amountByCategory) {
        return amountByCategory.values().stream().mapToLong(Long::longValue).sum();
    }

    private Map<Category, Long> calculateConsumptionRateByCategory(
        Map<Category, Long> currentExpenditureAmountByCategory,
        Map<Category, Long> criteriaExpenditureAmountByCategory) {
        return currentExpenditureAmountByCategory.keySet().stream().collect(toMap(identity(),
            category -> calculateConsumptionRate(currentExpenditureAmountByCategory.get(category),
                criteriaExpenditureAmountByCategory.get(category))));
    }

    private Long calculateConsumptionRate(Long currentAmount, Long criteriaAmount) {
        if (criteriaAmount == 0) criteriaAmount = 1L;
        return Math.round((double) currentAmount / criteriaAmount * 100); //% 단위로 변환하기 위해 곱하기 100
    }
}
