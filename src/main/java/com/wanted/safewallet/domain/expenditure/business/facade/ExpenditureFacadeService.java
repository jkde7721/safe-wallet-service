package com.wanted.safewallet.domain.expenditure.business.facade;

import com.wanted.safewallet.domain.budget.business.service.BudgetService;
import com.wanted.safewallet.domain.category.business.dto.CategoryValidationDto;
import com.wanted.safewallet.domain.category.business.service.CategoryService;
import com.wanted.safewallet.domain.category.persistence.entity.Category;
import com.wanted.safewallet.domain.expenditure.business.dto.ExpenditureSearchCond;
import com.wanted.safewallet.domain.expenditure.business.dto.ExpenditureSearchDto;
import com.wanted.safewallet.domain.expenditure.business.dto.ExpenditureSearchExceptsDto;
import com.wanted.safewallet.domain.expenditure.business.dto.ExpenditureStatsDateDto;
import com.wanted.safewallet.domain.expenditure.business.dto.ExpenditureStatsDto;
import com.wanted.safewallet.domain.expenditure.business.dto.ExpenditureUpdateDto;
import com.wanted.safewallet.domain.expenditure.business.dto.TodayExpenditureConsultDateDto;
import com.wanted.safewallet.domain.expenditure.business.dto.TodayExpenditureTotalConsultDto;
import com.wanted.safewallet.domain.expenditure.business.dto.YesterdayExpenditureTotalDailyStatsDto;
import com.wanted.safewallet.domain.expenditure.business.mapper.ExpenditureMapper;
import com.wanted.safewallet.domain.expenditure.business.service.ExpenditureConsultService;
import com.wanted.safewallet.domain.expenditure.business.service.ExpenditureDailyStatsService;
import com.wanted.safewallet.domain.expenditure.business.service.ExpenditureService;
import com.wanted.safewallet.domain.expenditure.persistence.entity.Expenditure;
import com.wanted.safewallet.domain.expenditure.web.dto.request.ExpenditureCreateRequest;
import com.wanted.safewallet.domain.expenditure.web.dto.request.ExpenditureSearchRequest;
import com.wanted.safewallet.domain.expenditure.web.dto.request.ExpenditureUpdateRequest;
import com.wanted.safewallet.domain.expenditure.web.dto.response.ExpenditureCreateResponse;
import com.wanted.safewallet.domain.expenditure.web.dto.response.ExpenditureDetailsResponse;
import com.wanted.safewallet.domain.expenditure.web.dto.response.ExpenditureSearchExceptsResponse;
import com.wanted.safewallet.domain.expenditure.web.dto.response.ExpenditureSearchResponse;
import com.wanted.safewallet.domain.expenditure.web.dto.response.ExpenditureStatsResponse;
import com.wanted.safewallet.domain.expenditure.web.dto.response.TodayExpenditureConsultResponse;
import com.wanted.safewallet.domain.expenditure.web.dto.response.YesterdayExpenditureDailyStatsResponse;
import com.wanted.safewallet.domain.expenditure.web.enums.StatsCriteria;
import java.time.LocalDate;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ExpenditureFacadeService {

    private final ExpenditureMapper expenditureMapper;
    private final BudgetService budgetService;
    private final CategoryService categoryService;
    private final ExpenditureService expenditureService;
    private final ExpenditureConsultService expenditureConsultService;
    private final ExpenditureDailyStatsService expenditureDailyStatsService;

    public ExpenditureDetailsResponse getExpenditureDetails(String userId, Long expenditureId) {
        Expenditure expenditure = expenditureService.getValidExpenditureWithCategoryAndImages(userId, expenditureId);
        return expenditureMapper.toDetailsResponse(expenditure);
    }

    public ExpenditureSearchResponse searchExpenditure(String userId,
        ExpenditureSearchRequest request, Pageable pageable) {
        ExpenditureSearchCond searchCond = expenditureMapper.toDto(request);
        ExpenditureSearchDto searchDto = expenditureService.searchExpenditure(userId, searchCond, pageable);
        return expenditureMapper.toResponse(searchDto);
    }

    public ExpenditureSearchExceptsResponse searchExpenditureExcepts(String userId,
        ExpenditureSearchRequest request) {
        ExpenditureSearchCond searchCond = expenditureMapper.toDto(request);
        ExpenditureSearchExceptsDto searchExceptsDto = expenditureService.searchExpenditureExcepts(userId, searchCond);
        return expenditureMapper.toResponse(searchExceptsDto);
    }

    @Transactional
    public ExpenditureCreateResponse createExpenditure(String userId, ExpenditureCreateRequest request) {
        validateRequest(request);
        Expenditure expenditure = expenditureMapper.toEntity(userId, request);
        Expenditure savedExpenditure = expenditureService.saveExpenditure(expenditure);
        return expenditureMapper.toResponse(savedExpenditure);
    }

    @Transactional
    public void updateExpenditure(String userId, Long expenditureId, ExpenditureUpdateRequest request) {
        validateRequest(request);
        Expenditure expenditure = expenditureService.getValidExpenditure(userId, expenditureId);
        ExpenditureUpdateDto updateDto = expenditureMapper.toDto(request);
        expenditureService.updateExpenditure(expenditure, updateDto);
    }

    @Transactional
    public void deleteExpenditure(String userId, Long expenditureId) {
        Expenditure expenditure = expenditureService.getValidExpenditure(userId, expenditureId);
        expenditureService.deleteExpenditure(expenditure);
    }

    public ExpenditureStatsResponse produceExpenditureStats(String userId, StatsCriteria criteria) {
        ExpenditureStatsDateDto expenditureStatsDateDto = new ExpenditureStatsDateDto(LocalDate.now(), criteria);
        ExpenditureStatsDto statsDto = expenditureService.produceExpenditureStats(userId, expenditureStatsDateDto);
        return expenditureMapper.toResponse(expenditureStatsDateDto, statsDto);
    }

    public TodayExpenditureConsultResponse consultTodayExpenditure(String userId) {
        TodayExpenditureConsultDateDto todayExpenditureConsultDateDto = new TodayExpenditureConsultDateDto(LocalDate.now());
        //월별 총 예산 (카테고리 별)
        Map<Category, Long> monthlyBudgetAmountByCategory = budgetService.getBudgetAmountByCategory(
            userId, todayExpenditureConsultDateDto.getBudgetYearMonth());
        //현재 월 내에서 어제까지 총 지출 (카테고리 별)
        Map<Category, Long> monthlyExpendedExpenditureAmountByCategory = expenditureService.getExpenditureAmountByCategory(
            userId, todayExpenditureConsultDateDto.getExpenditureStartDate(), todayExpenditureConsultDateDto.getExpenditureEndDate());
        TodayExpenditureTotalConsultDto consultDto = expenditureConsultService.consultTodayExpenditure(
            todayExpenditureConsultDateDto, monthlyBudgetAmountByCategory, monthlyExpendedExpenditureAmountByCategory);
        return expenditureMapper.toResponse(consultDto);
    }

    public YesterdayExpenditureDailyStatsResponse produceYesterdayExpenditureDailyStats(String userId) {
        LocalDate yesterday = LocalDate.now().minusDays(1); //ex. 10일이 어제라면 현재는 11일
        TodayExpenditureConsultDateDto yesterdayExpenditureConsultDateDto = new TodayExpenditureConsultDateDto(yesterday);
        //월별 총 예산 (카테고리 별)
        Map<Category, Long> monthlyBudgetAmountByCategory = budgetService.getBudgetAmountByCategory(
            userId, yesterdayExpenditureConsultDateDto.getBudgetYearMonth());
        //현재 월 내에서 그제까지 총 지출 (카테고리 별)
        Map<Category, Long> monthlyExpendedExpenditureAmountByCategory = expenditureService.getExpenditureAmountByCategory(
            userId, yesterdayExpenditureConsultDateDto.getExpenditureStartDate(), yesterdayExpenditureConsultDateDto.getExpenditureEndDate());
        //현재 월 내에서 남은 기간 동안 일별 적정 지출 (카테고리 별)
        Map<Category, Long> dailyConsultedExpenditureAmountByCategory = expenditureConsultService.getDailyConsultedExpenditureAmountByCategory(
            monthlyExpendedExpenditureAmountByCategory, monthlyBudgetAmountByCategory,
            yesterdayExpenditureConsultDateDto.getDaysOfCurrentMonth(),
            yesterdayExpenditureConsultDateDto.getLeftDaysOfCurrentMonth());
        //어제 하루 지출 (카테고리 별)
        Map<Category, Long> yesterdayExpenditureAmountByCategory = expenditureService.getExpenditureAmountByCategory(userId, yesterday);
        YesterdayExpenditureTotalDailyStatsDto dailyStatsDto = expenditureDailyStatsService.produceYesterdayExpenditureDailyStats(
            dailyConsultedExpenditureAmountByCategory, yesterdayExpenditureAmountByCategory);
        return expenditureMapper.toResponse(dailyStatsDto);
    }

    private void validateRequest(ExpenditureCreateRequest request) {
        CategoryValidationDto categoryValidationDto = new CategoryValidationDto(
            request.getCategoryId(), request.getType());
        categoryService.validateCategory(categoryValidationDto);
    }

    private void validateRequest(ExpenditureUpdateRequest request) {
        CategoryValidationDto categoryValidationDto = new CategoryValidationDto(
            request.getCategoryId(), request.getType());
        categoryService.validateCategory(categoryValidationDto);
    }
}
