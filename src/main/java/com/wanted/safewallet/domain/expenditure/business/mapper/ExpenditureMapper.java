package com.wanted.safewallet.domain.expenditure.business.mapper;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

import com.wanted.safewallet.domain.category.persistence.entity.Category;
import com.wanted.safewallet.domain.expenditure.business.dto.TodayExpenditureConsultDto;
import com.wanted.safewallet.domain.expenditure.business.dto.YesterdayExpenditureDailyStatsDto;
import com.wanted.safewallet.domain.expenditure.persistence.entity.Expenditure;
import com.wanted.safewallet.domain.expenditure.web.dto.request.ExpenditureCreateRequest;
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
import com.wanted.safewallet.domain.expenditure.web.enums.FinanceStatus;
import com.wanted.safewallet.domain.user.persistence.entity.User;
import com.wanted.safewallet.global.dto.response.PageResponse;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class ExpenditureMapper {

    public Expenditure toEntity(String userId, ExpenditureCreateRequest request) {
        return Expenditure.builder()
            .user(User.builder().id(userId).build())
            .category(Category.builder().id(request.getCategoryId()).build())
            .expenditureDate(request.getExpenditureDate())
            .amount(request.getAmount())
            .note(request.getNote()).build();
    }

    public ExpenditureCreateResponse toCreateDto(Expenditure expenditure) {
        return new ExpenditureCreateResponse(expenditure.getId());
    }

    public ExpenditureDetailsResponse toDetailsDto(Expenditure expenditure) {
        return ExpenditureDetailsResponse.builder()
            .expenditureDate(expenditure.getExpenditureDate())
            .amount(expenditure.getAmount())
            .categoryId(expenditure.getCategory().getId())
            .type(expenditure.getCategory().getType())
            .title(expenditure.getTitle())
            .note(expenditure.getNote())
            .imageUrls(expenditure.getImageUrls()).build();
    }

    public ExpenditureSearchResponse toSearchDto(long totalAmount,
        Map<Category, Long> expenditureAmountByCategory, Page<Expenditure> expenditurePage) {
        Map<LocalDate, List<Expenditure>> expenditureListByDate = expenditurePage.getContent().stream()
            .collect(groupingBy(expenditure -> expenditure.getExpenditureDate().toLocalDate(), toList()));
        return ExpenditureSearchResponse.builder()
            .totalAmount(totalAmount)
            .expenditureAmountOfCategoryList(toListOfCategoryDto(expenditureAmountByCategory))
            .expenditureListByDate(toListByDateDto(expenditureListByDate))
            .paging(new PageResponse(expenditurePage)).build();
    }

    public ExpenditureSearchExceptsResponse toSearchExceptsDto(long totalAmount,
        Map<Category, Long> expenditureAmountByCategory) {
        return ExpenditureSearchExceptsResponse.builder()
            .totalAmount(totalAmount)
            .expenditureAmountOfCategoryList(toListOfCategoryDto(expenditureAmountByCategory))
            .build();
    }

    public ExpenditureStatsResponse toDto(LocalDate currentStartDate, LocalDate currentEndDate,
        LocalDate criteriaStartDate, LocalDate criteriaEndDate, Long totalConsumptionRate,
        Map<Category, Long> consumptionRateByCategory) {
        List<ConsumptionRateOfCategoryResponse> consumptionRateOfCategoryList =
            consumptionRateByCategory.keySet().stream()
                .sorted(Comparator.comparing(Category::getId))
                .map(category -> new ConsumptionRateOfCategoryResponse(category.getId(), category.getType(),
                    consumptionRateByCategory.get(category)))
                .toList();
        return ExpenditureStatsResponse.builder()
            .currentStartDate(currentStartDate).currentEndDate(currentEndDate)
            .criteriaStartDate(criteriaStartDate).criteriaEndDate(criteriaEndDate)
            .totalConsumptionRate(totalConsumptionRate)
            .consumptionRateOfCategoryList(consumptionRateOfCategoryList).build();
    }

    private List<ExpenditureAmountOfCategoryResponse> toListOfCategoryDto(
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

    private List<ExpenditureListByDateResponse> toListByDateDto(
        Map<LocalDate, List<Expenditure>> expenditureListByDate) {
        return expenditureListByDate.keySet().stream()
            .sorted(Comparator.reverseOrder())
            .map(date -> ExpenditureListByDateResponse.builder()
                .expenditureDate(date)
                .expenditureList(toSubListByDateDto(expenditureListByDate.get(date)))
                .build())
            .toList();
    }

    private List<ExpenditureResponse> toSubListByDateDto(List<Expenditure> expenditureList) {
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

    public TodayExpenditureConsultResponse toDto(long totalAmount, FinanceStatus totalFinanceStatus,
        Map<Category, TodayExpenditureConsultDto> todayExpenditureConsultByCategory) {
        List<TodayExpenditureConsultOfCategoryResponse> todayExpenditureConsultOfCategoryList =
            todayExpenditureConsultByCategory.keySet().stream()
                .sorted(Comparator.comparing(Category::getId))
                .map(category -> new TodayExpenditureConsultOfCategoryResponse(category.getId(), category.getType(),
                    todayExpenditureConsultByCategory.get(category).amount(),
                    todayExpenditureConsultByCategory.get(category).financeStatus()))
                .toList();
        return new TodayExpenditureConsultResponse(totalAmount, totalFinanceStatus, todayExpenditureConsultOfCategoryList);
    }

    public YesterdayExpenditureDailyStatsResponse toDto(Long totalAmount,
        Map<Category, YesterdayExpenditureDailyStatsDto> yesterdayExpenditureDailyStatsByCategory) {
        List<YesterdayExpenditureDailyStatsOfCategoryResponse> yesterdayExpenditureDailyStatsOfCategoryList =
            yesterdayExpenditureDailyStatsByCategory.keySet().stream()
                .sorted(Comparator.comparing(Category::getId))
                .map(category -> new YesterdayExpenditureDailyStatsOfCategoryResponse(category.getId(), category.getType(),
                    yesterdayExpenditureDailyStatsByCategory.get(category).consultedAmount(),
                    yesterdayExpenditureDailyStatsByCategory.get(category).expendedAmount(),
                    yesterdayExpenditureDailyStatsByCategory.get(category).consumptionRate()))
                .toList();
        return new YesterdayExpenditureDailyStatsResponse(totalAmount, yesterdayExpenditureDailyStatsOfCategoryList);
    }
}
