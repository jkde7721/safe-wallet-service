package com.wanted.safewallet.domain.expenditure.business.mapper;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

import com.wanted.safewallet.domain.category.persistence.entity.Category;
import com.wanted.safewallet.domain.expenditure.business.dto.ExpenditureSearchCond;
import com.wanted.safewallet.domain.expenditure.business.dto.ExpenditureSearchDto;
import com.wanted.safewallet.domain.expenditure.business.dto.ExpenditureSearchExceptsDto;
import com.wanted.safewallet.domain.expenditure.business.dto.ExpenditureStatsDateDto;
import com.wanted.safewallet.domain.expenditure.business.dto.ExpenditureStatsDto;
import com.wanted.safewallet.domain.expenditure.business.dto.ExpenditureUpdateDto;
import com.wanted.safewallet.domain.expenditure.business.dto.TodayExpenditureConsultDto;
import com.wanted.safewallet.domain.expenditure.business.dto.TodayExpenditureTotalConsultDto;
import com.wanted.safewallet.domain.expenditure.business.dto.YesterdayExpenditureDailyStatsDto;
import com.wanted.safewallet.domain.expenditure.business.dto.YesterdayExpenditureTotalDailyStatsDto;
import com.wanted.safewallet.domain.expenditure.persistence.entity.Expenditure;
import com.wanted.safewallet.domain.expenditure.web.dto.request.ExpenditureCreateRequest;
import com.wanted.safewallet.domain.expenditure.web.dto.request.ExpenditureSearchRequest;
import com.wanted.safewallet.domain.expenditure.web.dto.request.ExpenditureUpdateRequest;
import com.wanted.safewallet.domain.expenditure.web.dto.response.ExpenditureCreateResponse;
import com.wanted.safewallet.domain.expenditure.web.dto.response.ExpenditureDetailsResponse;
import com.wanted.safewallet.domain.expenditure.web.dto.response.ExpenditureListByDateResponse;
import com.wanted.safewallet.domain.expenditure.web.dto.response.ExpenditureListByDateResponse.ExpenditureResponse;
import com.wanted.safewallet.domain.expenditure.web.dto.response.ExpenditureSearchResponse;
import com.wanted.safewallet.domain.expenditure.web.dto.response.ExpenditureSearchExceptsResponse;
import com.wanted.safewallet.domain.expenditure.web.dto.response.ExpenditureStatsResponse;
import com.wanted.safewallet.domain.expenditure.web.dto.response.ExpenditureStatsResponse.ConsumptionRateOfCategoryResponse;
import com.wanted.safewallet.domain.expenditure.web.dto.response.TodayExpenditureConsultResponse;
import com.wanted.safewallet.domain.expenditure.web.dto.response.TodayExpenditureConsultResponse.TodayExpenditureConsultOfCategoryResponse;
import com.wanted.safewallet.domain.expenditure.web.dto.response.YesterdayExpenditureDailyStatsResponse;
import com.wanted.safewallet.domain.expenditure.web.dto.response.YesterdayExpenditureDailyStatsResponse.YesterdayExpenditureDailyStatsOfCategoryResponse;
import com.wanted.safewallet.domain.expenditure.web.dto.response.ExpenditureAmountOfCategoryResponse;
import com.wanted.safewallet.domain.user.persistence.entity.User;
import com.wanted.safewallet.global.dto.response.PageResponse;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class ExpenditureMapper {

    public ExpenditureSearchCond toDto(ExpenditureSearchRequest request) {
        return new ExpenditureSearchCond(request.getStartDate(), request.getEndDate(),
            request.getCategories(), request.getMinAmount(), request.getMaxAmount(), request.getExcepts());
    }

    public Expenditure toEntity(String userId, ExpenditureCreateRequest request) {
        return Expenditure.builder()
            .user(User.builder().id(userId).build())
            .category(Category.builder().id(request.getCategoryId()).type(request.getType()).build())
            .expenditureDate(request.getExpenditureDate())
            .amount(request.getAmount())
            .title(request.getTitle())
            .note(request.getNote()).build();
    }

    public ExpenditureUpdateDto toDto(ExpenditureUpdateRequest request) {
        return ExpenditureUpdateDto.builder()
            .expenditureDate(request.getExpenditureDate())
            .amount(request.getAmount())
            .categoryId(request.getCategoryId())
            .type(request.getType())
            .title(request.getTitle())
            .note(request.getNote()).build();
    }

    public ExpenditureCreateResponse toResponse(Expenditure expenditure) {
        return new ExpenditureCreateResponse(expenditure.getId());
    }

    public ExpenditureDetailsResponse toDetailsResponse(Expenditure expenditure) {
        return ExpenditureDetailsResponse.builder()
            .expenditureDate(expenditure.getExpenditureDate())
            .amount(expenditure.getAmount())
            .categoryId(expenditure.getCategory().getId())
            .type(expenditure.getCategory().getType())
            .title(expenditure.getTitle())
            .note(expenditure.getNote())
            .imageUrls(expenditure.getImageUrls()).build();
    }

    public ExpenditureSearchResponse toResponse(ExpenditureSearchDto searchDto) {
        Map<LocalDate, List<Expenditure>> expenditureListByDate = searchDto.getExpenditurePage().getContent().stream()
            .collect(groupingBy(expenditure -> expenditure.getExpenditureDate().toLocalDate(), toList()));
        return ExpenditureSearchResponse.builder()
            .totalAmount(searchDto.getTotalAmount())
            .expenditureAmountOfCategoryList(toListOfCategoryResponse(searchDto.getExpenditureAmountByCategory()))
            .expenditureListByDate(toListByDateResponse(expenditureListByDate))
            .paging(new PageResponse(searchDto.getExpenditurePage())).build();
    }

    public ExpenditureSearchExceptsResponse toResponse(ExpenditureSearchExceptsDto searchExceptsDto) {
        return ExpenditureSearchExceptsResponse.builder()
            .totalAmount(searchExceptsDto.getTotalAmount())
            .expenditureAmountOfCategoryList(
                toListOfCategoryResponse(searchExceptsDto.getExpenditureAmountByCategory()))
            .build();
    }

    public ExpenditureStatsResponse toResponse(ExpenditureStatsDateDto expenditureStatsDateDto,
        ExpenditureStatsDto statsDto) {
        Map<Category, Long> consumptionRateByCategory = statsDto.getConsumptionRateByCategory();
        List<ConsumptionRateOfCategoryResponse> consumptionRateOfCategoryList =
            consumptionRateByCategory.keySet().stream()
                .sorted(Comparator.comparing(Category::getId))
                .map(category -> new ConsumptionRateOfCategoryResponse(category.getId(), category.getType(),
                    consumptionRateByCategory.get(category)))
                .toList();
        return ExpenditureStatsResponse.builder()
            .currentStartDate(expenditureStatsDateDto.getCurrentStartDate())
            .currentEndDate(expenditureStatsDateDto.getCurrentEndDate())
            .criteriaStartDate(expenditureStatsDateDto.getCriteriaStartDate())
            .criteriaEndDate(expenditureStatsDateDto.getCriteriaEndDate())
            .totalConsumptionRate(statsDto.getTotalConsumptionRate())
            .consumptionRateOfCategoryList(consumptionRateOfCategoryList).build();
    }

    private List<ExpenditureAmountOfCategoryResponse> toListOfCategoryResponse(
        Map<Category, Long> expenditureAmountByCategory) {
        return expenditureAmountByCategory.keySet().stream()
            .sorted(Comparator.comparing(Category::getId))
            .map(category -> ExpenditureAmountOfCategoryResponse.builder()
                .categoryId(category.getId())
                .type(category.getType())
                .amount(expenditureAmountByCategory.get(category))
                .build())
            .toList();
    }

    private List<ExpenditureListByDateResponse> toListByDateResponse(
        Map<LocalDate, List<Expenditure>> expenditureListByDate) {
        return expenditureListByDate.keySet().stream()
            .sorted(Comparator.reverseOrder())
            .map(date -> ExpenditureListByDateResponse.builder()
                .expenditureDate(date)
                .expenditureList(toSubListByDateResponse(expenditureListByDate.get(date)))
                .build())
            .toList();
    }

    private List<ExpenditureResponse> toSubListByDateResponse(List<Expenditure> expenditureList) {
        return expenditureList.stream()
            .sorted((e1, e2) -> e2.getAmount().compareTo(e1.getAmount()))
            .map(expenditure -> ExpenditureResponse.builder()
                .expenditureId(expenditure.getId())
                .amount(expenditure.getAmount())
                .categoryId(expenditure.getCategory().getId())
                .type(expenditure.getCategory().getType())
                .title(expenditure.getTitle())
                .build())
            .toList();
    }

    public TodayExpenditureConsultResponse toResponse(TodayExpenditureTotalConsultDto consultDto) {
        Map<Category, TodayExpenditureConsultDto> todayExpenditureConsultByCategory = consultDto.getTodayExpenditureConsultByCategory();
        List<TodayExpenditureConsultOfCategoryResponse> todayExpenditureConsultOfCategoryList =
            todayExpenditureConsultByCategory.keySet().stream()
                .sorted(Comparator.comparing(Category::getId))
                .map(category -> new TodayExpenditureConsultOfCategoryResponse(category.getId(), category.getType(),
                    todayExpenditureConsultByCategory.get(category).amount(),
                    todayExpenditureConsultByCategory.get(category).financeStatus()))
                .toList();
        return new TodayExpenditureConsultResponse(consultDto.getTotalAmount(), consultDto.getTotalFinanceStatus(),
            todayExpenditureConsultOfCategoryList);
    }

    public YesterdayExpenditureDailyStatsResponse toResponse(YesterdayExpenditureTotalDailyStatsDto dailyStatsDto) {
        Map<Category, YesterdayExpenditureDailyStatsDto> yesterdayExpenditureDailyStatsByCategory = dailyStatsDto.getYesterdayExpenditureDailyStatsByCategory();
        List<YesterdayExpenditureDailyStatsOfCategoryResponse> yesterdayExpenditureDailyStatsOfCategoryList =
            yesterdayExpenditureDailyStatsByCategory.keySet().stream()
                .sorted(Comparator.comparing(Category::getId))
                .map(category -> new YesterdayExpenditureDailyStatsOfCategoryResponse(category.getId(), category.getType(),
                    yesterdayExpenditureDailyStatsByCategory.get(category).consultedAmount(),
                    yesterdayExpenditureDailyStatsByCategory.get(category).expendedAmount(),
                    yesterdayExpenditureDailyStatsByCategory.get(category).consumptionRate()))
                .toList();
        return new YesterdayExpenditureDailyStatsResponse(dailyStatsDto.getTotalAmount(), yesterdayExpenditureDailyStatsOfCategoryList);
    }
}
