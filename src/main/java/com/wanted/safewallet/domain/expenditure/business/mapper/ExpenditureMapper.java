package com.wanted.safewallet.domain.expenditure.business.mapper;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

import com.wanted.safewallet.domain.category.persistence.entity.Category;
import com.wanted.safewallet.domain.expenditure.business.dto.TodayExpenditureConsultDto;
import com.wanted.safewallet.domain.expenditure.business.dto.YesterdayExpenditureDailyStatsDto;
import com.wanted.safewallet.domain.expenditure.persistence.entity.Expenditure;
import com.wanted.safewallet.domain.expenditure.web.dto.request.ExpenditureCreateRequestDto;
import com.wanted.safewallet.domain.expenditure.web.dto.response.ExpenditureCreateResponseDto;
import com.wanted.safewallet.domain.expenditure.web.dto.response.ExpenditureDetailsResponseDto;
import com.wanted.safewallet.domain.expenditure.web.dto.response.ExpenditureListByDateResponseDto;
import com.wanted.safewallet.domain.expenditure.web.dto.response.ExpenditureListByDateResponseDto.ExpenditureResponseDto;
import com.wanted.safewallet.domain.expenditure.web.dto.response.ExpenditureSearchResponseDto;
import com.wanted.safewallet.domain.expenditure.web.dto.response.ExpenditureSearchExceptsResponseDto;
import com.wanted.safewallet.domain.expenditure.web.dto.response.ExpenditureStatsResponseDto;
import com.wanted.safewallet.domain.expenditure.web.dto.response.ExpenditureStatsResponseDto.ConsumptionRateOfCategoryResponseDto;
import com.wanted.safewallet.domain.expenditure.web.dto.response.TodayExpenditureConsultResponseDto;
import com.wanted.safewallet.domain.expenditure.web.dto.response.TodayExpenditureConsultResponseDto.TodayExpenditureConsultOfCategoryResponseDto;
import com.wanted.safewallet.domain.expenditure.web.dto.response.YesterdayExpenditureDailyStatsResponseDto;
import com.wanted.safewallet.domain.expenditure.web.dto.response.YesterdayExpenditureDailyStatsResponseDto.YesterdayExpenditureDailyStatsOfCategoryResponseDto;
import com.wanted.safewallet.domain.expenditure.web.dto.response.ExpenditureAmountOfCategoryResponseDto;
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

    public Expenditure toEntity(String userId, ExpenditureCreateRequestDto requestDto) {
        return Expenditure.builder()
            .user(User.builder().id(userId).build())
            .category(Category.builder().id(requestDto.getCategoryId()).build())
            .expenditureDate(requestDto.getExpenditureDate())
            .amount(requestDto.getAmount())
            .note(requestDto.getNote()).build();
    }

    public ExpenditureCreateResponseDto toCreateDto(Expenditure expenditure) {
        return new ExpenditureCreateResponseDto(expenditure.getId());
    }

    public ExpenditureDetailsResponseDto toDetailsDto(Expenditure expenditure) {
        return ExpenditureDetailsResponseDto.builder()
            .expenditureDate(expenditure.getExpenditureDate())
            .amount(expenditure.getAmount())
            .categoryId(expenditure.getCategory().getId())
            .type(expenditure.getCategory().getType())
            .title(expenditure.getTitle())
            .note(expenditure.getNote())
            .imageUrls(expenditure.getImageUrls()).build();
    }

    public ExpenditureSearchResponseDto toSearchDto(long totalAmount,
        Map<Category, Long> expenditureAmountByCategory, Page<Expenditure> expenditurePage) {
        Map<LocalDate, List<Expenditure>> expenditureListByDate = expenditurePage.getContent().stream()
            .collect(groupingBy(expenditure -> expenditure.getExpenditureDate().toLocalDate(), toList()));
        return ExpenditureSearchResponseDto.builder()
            .totalAmount(totalAmount)
            .expenditureAmountOfCategoryList(toListOfCategoryDto(expenditureAmountByCategory))
            .expenditureListByDate(toListByDateDto(expenditureListByDate))
            .paging(new PageResponse(expenditurePage)).build();
    }

    public ExpenditureSearchExceptsResponseDto toSearchExceptsDto(long totalAmount,
        Map<Category, Long> expenditureAmountByCategory) {
        return ExpenditureSearchExceptsResponseDto.builder()
            .totalAmount(totalAmount)
            .expenditureAmountOfCategoryList(toListOfCategoryDto(expenditureAmountByCategory))
            .build();
    }

    public ExpenditureStatsResponseDto toDto(LocalDate currentStartDate, LocalDate currentEndDate,
        LocalDate criteriaStartDate, LocalDate criteriaEndDate, Long totalConsumptionRate,
        Map<Category, Long> consumptionRateByCategory) {
        List<ConsumptionRateOfCategoryResponseDto> consumptionRateOfCategoryList =
            consumptionRateByCategory.keySet().stream()
                .sorted(Comparator.comparing(Category::getId))
                .map(category -> new ConsumptionRateOfCategoryResponseDto(category.getId(), category.getType(),
                    consumptionRateByCategory.get(category)))
                .toList();
        return ExpenditureStatsResponseDto.builder()
            .currentStartDate(currentStartDate).currentEndDate(currentEndDate)
            .criteriaStartDate(criteriaStartDate).criteriaEndDate(criteriaEndDate)
            .totalConsumptionRate(totalConsumptionRate)
            .consumptionRateOfCategoryList(consumptionRateOfCategoryList).build();
    }

    private List<ExpenditureAmountOfCategoryResponseDto> toListOfCategoryDto(
        Map<Category, Long> expenditureAmountByCategory) {
        return expenditureAmountByCategory.keySet().stream()
            .sorted(Comparator.comparing(Category::getId))
            .map(category -> ExpenditureAmountOfCategoryResponseDto.builder()
                .categoryId(category.getId())
                .type(category.getType())
                .amount(expenditureAmountByCategory.get(category))
                .build())
            .toList();
    }

    private List<ExpenditureListByDateResponseDto> toListByDateDto(
        Map<LocalDate, List<Expenditure>> expenditureListByDate) {
        return expenditureListByDate.keySet().stream()
            .sorted(Comparator.reverseOrder())
            .map(date -> ExpenditureListByDateResponseDto.builder()
                .expenditureDate(date)
                .expenditureList(toSubListByDateDto(expenditureListByDate.get(date)))
                .build())
            .toList();
    }

    private List<ExpenditureResponseDto> toSubListByDateDto(List<Expenditure> expenditureList) {
        return expenditureList.stream()
            .sorted((e1, e2) -> e2.getAmount().compareTo(e1.getAmount()))
            .map(expenditure -> ExpenditureResponseDto.builder()
                .expenditureId(expenditure.getId())
                .amount(expenditure.getAmount())
                .categoryId(expenditure.getCategory().getId())
                .type(expenditure.getCategory().getType())
                .title(expenditure.getTitle())
                .build())
            .toList();
    }

    public TodayExpenditureConsultResponseDto toDto(long totalAmount, FinanceStatus totalFinanceStatus,
        Map<Category, TodayExpenditureConsultDto> todayExpenditureConsultByCategory) {
        List<TodayExpenditureConsultOfCategoryResponseDto> todayExpenditureConsultOfCategoryList =
            todayExpenditureConsultByCategory.keySet().stream()
                .sorted(Comparator.comparing(Category::getId))
                .map(category -> new TodayExpenditureConsultOfCategoryResponseDto(category.getId(), category.getType(),
                    todayExpenditureConsultByCategory.get(category).amount(),
                    todayExpenditureConsultByCategory.get(category).financeStatus()))
                .toList();
        return new TodayExpenditureConsultResponseDto(totalAmount, totalFinanceStatus, todayExpenditureConsultOfCategoryList);
    }

    public YesterdayExpenditureDailyStatsResponseDto toDto(Long totalAmount,
        Map<Category, YesterdayExpenditureDailyStatsDto> yesterdayExpenditureDailyStatsByCategory) {
        List<YesterdayExpenditureDailyStatsOfCategoryResponseDto> yesterdayExpenditureDailyStatsOfCategoryList =
            yesterdayExpenditureDailyStatsByCategory.keySet().stream()
                .sorted(Comparator.comparing(Category::getId))
                .map(category -> new YesterdayExpenditureDailyStatsOfCategoryResponseDto(category.getId(), category.getType(),
                    yesterdayExpenditureDailyStatsByCategory.get(category).consultedAmount(),
                    yesterdayExpenditureDailyStatsByCategory.get(category).expendedAmount(),
                    yesterdayExpenditureDailyStatsByCategory.get(category).consumptionRate()))
                .toList();
        return new YesterdayExpenditureDailyStatsResponseDto(totalAmount, yesterdayExpenditureDailyStatsOfCategoryList);
    }
}
