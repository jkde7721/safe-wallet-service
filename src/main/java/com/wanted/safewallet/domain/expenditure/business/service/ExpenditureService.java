package com.wanted.safewallet.domain.expenditure.business.service;

import static com.wanted.safewallet.domain.expenditure.business.service.ExpenditureConsultService.CACHE_NAME;
import static com.wanted.safewallet.domain.expenditure.web.enums.StatsCriteria.LAST_MONTH;
import static com.wanted.safewallet.domain.expenditure.web.enums.StatsCriteria.LAST_YEAR;
import static com.wanted.safewallet.global.exception.ErrorCode.FORBIDDEN_EXPENDITURE;
import static com.wanted.safewallet.global.exception.ErrorCode.NOT_FOUND_EXPENDITURE;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.TemporalAdjusters.previousOrSame;
import static java.util.stream.Collectors.toMap;

import com.wanted.safewallet.domain.category.business.dto.request.CategoryValidRequestDto;
import com.wanted.safewallet.domain.category.business.service.CategoryService;
import com.wanted.safewallet.domain.category.persistence.entity.Category;
import com.wanted.safewallet.domain.expenditure.business.mapper.ExpenditureMapper;
import com.wanted.safewallet.domain.expenditure.business.vo.ExpenditureDateUpdateVo;
import com.wanted.safewallet.domain.expenditure.persistence.dto.response.StatsByCategoryResponseDto;
import com.wanted.safewallet.domain.expenditure.persistence.dto.response.TotalAmountByCategoryResponseDto;
import com.wanted.safewallet.domain.expenditure.persistence.entity.Expenditure;
import com.wanted.safewallet.domain.expenditure.persistence.repository.ExpenditureRepository;
import com.wanted.safewallet.domain.expenditure.web.dto.request.ExpenditureCreateRequestDto;
import com.wanted.safewallet.domain.expenditure.web.dto.request.ExpenditureSearchCond;
import com.wanted.safewallet.domain.expenditure.web.dto.request.ExpenditureUpdateRequestDto;
import com.wanted.safewallet.domain.expenditure.web.dto.response.ExpenditureCreateResponseDto;
import com.wanted.safewallet.domain.expenditure.web.dto.response.ExpenditureDetailsResponseDto;
import com.wanted.safewallet.domain.expenditure.web.dto.response.ExpenditureSearchResponseDto;
import com.wanted.safewallet.domain.expenditure.web.dto.response.ExpenditureSearchExceptsResponseDto;
import com.wanted.safewallet.domain.expenditure.web.dto.response.ExpenditureStatsResponseDto;
import com.wanted.safewallet.domain.expenditure.web.enums.StatsCriteria;
import com.wanted.safewallet.global.exception.BusinessException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ExpenditureService {

    private final ExpenditureMapper expenditureMapper;
    private final CategoryService categoryService;
    private final ExpenditureRepository expenditureRepository;

    public ExpenditureDetailsResponseDto getExpenditureDetails(String userId, Long expenditureId) {
        Expenditure expenditure = getValidExpenditureWithCategory(userId, expenditureId);
        return expenditureMapper.toDetailsDto(expenditure);
    }

    public ExpenditureSearchResponseDto searchExpenditure(String userId,
        ExpenditureSearchCond searchCond, Pageable pageable) {
        long totalAmount = expenditureRepository.getTotalAmount(userId, searchCond);
        List<StatsByCategoryResponseDto> statsByCategory = expenditureRepository.getStatsByCategory(userId, searchCond);
        Page<Expenditure> expenditurePage = expenditureRepository.findAllFetch(userId, searchCond, pageable);
        return expenditureMapper.toSearchDto(totalAmount, statsByCategory, expenditurePage);
    }

    public ExpenditureSearchExceptsResponseDto searchExpenditureExcepts(String userId,
        ExpenditureSearchCond searchCond) {
        long totalAmount = expenditureRepository.getTotalAmount(userId, searchCond);
        List<StatsByCategoryResponseDto> statsByCategory = expenditureRepository.getStatsByCategory(userId, searchCond);
        return expenditureMapper.toSearchExceptsDto(totalAmount, statsByCategory);
    }

    @CacheEvict(cacheNames = CACHE_NAME, key = "#userId", condition =
        "#root.target.isCurrentYearMonthAndBeforeCurrentDate(#requestDto.expenditureDate)")
    @Transactional
    public ExpenditureCreateResponseDto createExpenditure(String userId,
        ExpenditureCreateRequestDto requestDto) {
        validateRequest(requestDto);
        Expenditure expenditure = expenditureMapper.toEntity(userId, requestDto);
        Expenditure savedExpenditure = expenditureRepository.save(expenditure);
        return expenditureMapper.toCreateDto(savedExpenditure);
    }

    @CacheEvict(cacheNames = CACHE_NAME, key = "#userId", condition = """
        #root.target.isCurrentYearMonthAndBeforeCurrentDate(#result.originalExpenditureDate()) ||
        #root.target.isCurrentYearMonthAndBeforeCurrentDate(#result.updatedExpenditureDate())""")
    @Transactional
    public ExpenditureDateUpdateVo updateExpenditure(String userId, Long expenditureId, ExpenditureUpdateRequestDto requestDto) {
        validateRequest(requestDto);
        Expenditure expenditure = getValidExpenditure(userId, expenditureId);
        LocalDateTime originalExpenditureDate = expenditure.getExpenditureDate();
        expenditure.update(requestDto.getCategoryId(), requestDto.getExpenditureDate(),
            requestDto.getAmount(), requestDto.getNote());
        return new ExpenditureDateUpdateVo(originalExpenditureDate, requestDto.getExpenditureDate());
    }

    @CacheEvict(cacheNames = CACHE_NAME, key = "#userId", condition =
        "#root.target.isCurrentYearMonthAndBeforeCurrentDate(#result)")
    @Transactional
    public LocalDateTime deleteExpenditure(String userId, Long expenditureId) {
        Expenditure expenditure = getValidExpenditure(userId, expenditureId);
        expenditure.softDelete();
        return expenditure.getExpenditureDate();
    }

    public ExpenditureStatsResponseDto produceExpenditureStats(String userId, StatsCriteria criteria) {
        LocalDate currentEndDate = LocalDate.now();
        LocalDate currentStartDate = getCurrentStartDate(currentEndDate, criteria);
        LocalDate criteriaStartDate = getCriteriaStartDate(currentStartDate, criteria);
        LocalDate criteriaEndDate = getCriteriaEndDate(criteriaStartDate, DAYS.between(currentStartDate, currentEndDate));

        List<TotalAmountByCategoryResponseDto> currentTotalAmountList =
            expenditureRepository.getTotalAmountByCategoryList(userId, currentStartDate, currentEndDate);
        List<TotalAmountByCategoryResponseDto> criteriaTotalAmountList =
            expenditureRepository.getTotalAmountByCategoryList(userId, criteriaStartDate, criteriaEndDate);

        Long totalConsumptionRate = calculateTotalConsumptionRate(currentTotalAmountList, criteriaTotalAmountList);
        Map<Category, Long> consumptionRateByCategory = calculateConsumptionRateByCategory(currentTotalAmountList, criteriaTotalAmountList);
        return expenditureMapper.toDto(currentStartDate, currentEndDate, criteriaStartDate, criteriaEndDate,
            totalConsumptionRate, consumptionRateByCategory);
    }

    public boolean isCurrentYearMonthAndBeforeCurrentDate(LocalDateTime expenditureDate) {
        LocalDate now = LocalDate.now();
        LocalDateTime startDateTime = now.withDayOfMonth(1).atStartOfDay();
        LocalDateTime endDateTime = now.atStartOfDay();
        return (expenditureDate.isEqual(startDateTime) || expenditureDate.isAfter(startDateTime)) &&
            expenditureDate.isBefore(endDateTime);
    }

    public Expenditure getValidExpenditure(String userId, Long expenditureId) {
        Expenditure expenditure = getExpenditure(expenditureId);
        if (Objects.equals(expenditure.getUser().getId(), userId)) {
            return expenditure;
        }
        throw new BusinessException(FORBIDDEN_EXPENDITURE);
    }

    public Expenditure getValidExpenditureWithCategory(String userId, Long expenditureId) {
        Expenditure expenditure = getExpenditureWithCategory(expenditureId);
        if (Objects.equals(expenditure.getUser().getId(), userId)) {
            return expenditure;
        }
        throw new BusinessException(FORBIDDEN_EXPENDITURE);
    }

    public Expenditure getExpenditure(Long expenditureId) {
        return expenditureRepository.findById(expenditureId)
            .orElseThrow(() -> new BusinessException(NOT_FOUND_EXPENDITURE));
    }

    public Expenditure getExpenditureWithCategory(Long expenditureId) {
        return expenditureRepository.findByIdFetch(expenditureId)
            .orElseThrow(() -> new BusinessException(NOT_FOUND_EXPENDITURE));
    }

    private LocalDate getCurrentStartDate(LocalDate currentEndDate, StatsCriteria criteria) {
        if (criteria == LAST_YEAR) {
            return LocalDate.of(currentEndDate.getYear(), 1, 1);
        }
        else if (criteria == LAST_MONTH) {
            return LocalDate.of(currentEndDate.getYear(), currentEndDate.getMonth(), 1);
        }
        else {
            return currentEndDate.with(previousOrSame(DayOfWeek.MONDAY));
        }
    }

    private LocalDate getCriteriaStartDate(LocalDate currentStartDate, StatsCriteria criteria) {
        if (criteria == LAST_YEAR) {
            return currentStartDate.minusYears(1);
        }
        else if (criteria == LAST_MONTH) {
            return currentStartDate.minusMonths(1);
        }
        else {
            return currentStartDate.minusWeeks(1);
        }
    }

    private LocalDate getCriteriaEndDate(LocalDate criteriaStartDate, long durationOfDays) {
        return criteriaStartDate.plusDays(durationOfDays);
    }

    private Long calculateTotalConsumptionRate(
        List<TotalAmountByCategoryResponseDto> currentTotalAmountList,
        List<TotalAmountByCategoryResponseDto> criteriaTotalAmountList) {
        long currentTotalAmount = currentTotalAmountList.stream()
            .mapToLong(TotalAmountByCategoryResponseDto::getTotalAmount).sum();
        long criteriaTotalAmount = criteriaTotalAmountList.stream()
            .mapToLong(TotalAmountByCategoryResponseDto::getTotalAmount).sum();
        return calculateConsumptionRate(currentTotalAmount, criteriaTotalAmount);
    }

    private Map<Category, Long> calculateConsumptionRateByCategory(
        List<TotalAmountByCategoryResponseDto> currentTotalAmountList,
        List<TotalAmountByCategoryResponseDto> criteriaTotalAmountList) {
        Map<Category, Long> currentTotalAmountByCategory = currentTotalAmountList.stream()
            .collect(toMap(TotalAmountByCategoryResponseDto::getCategory,
                TotalAmountByCategoryResponseDto::getTotalAmount));
        Map<Category, Long> criteriaTotalAmountByCategory = criteriaTotalAmountList.stream()
            .collect(toMap(TotalAmountByCategoryResponseDto::getCategory,
                TotalAmountByCategoryResponseDto::getTotalAmount));
        return currentTotalAmountByCategory.keySet().stream().collect(toMap(Function.identity(),
            category -> calculateConsumptionRate(currentTotalAmountByCategory.get(category),
                criteriaTotalAmountByCategory.get(category))));
    }

    private Long calculateConsumptionRate(Long currentAmount, Long criteriaAmount) {
        if (criteriaAmount == 0) criteriaAmount = 1L;
        return Math.round((double) currentAmount / criteriaAmount * 100); //% 단위로 변환하기 위해 곱하기 100
    }

    private void validateRequest(ExpenditureCreateRequestDto requestDto) {
        CategoryValidRequestDto categoryValidDto = new CategoryValidRequestDto(
            requestDto.getCategoryId(), requestDto.getType());
        categoryService.validateCategory(categoryValidDto);
    }

    private void validateRequest(ExpenditureUpdateRequestDto requestDto) {
        CategoryValidRequestDto categoryValidDto = new CategoryValidRequestDto(
            requestDto.getCategoryId(), requestDto.getType());
        categoryService.validateCategory(categoryValidDto);
    }
}
