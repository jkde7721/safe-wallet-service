package com.wanted.safewallet.domain.expenditure.business.mapper;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

import com.wanted.safewallet.domain.category.persistence.entity.Category;
import com.wanted.safewallet.domain.expenditure.persistence.dto.response.StatsByCategoryResponseDto;
import com.wanted.safewallet.domain.expenditure.persistence.entity.Expenditure;
import com.wanted.safewallet.domain.expenditure.web.dto.request.ExpenditureCreateRequestDto;
import com.wanted.safewallet.domain.expenditure.web.dto.response.ExpenditureCreateResponseDto;
import com.wanted.safewallet.domain.expenditure.web.dto.response.ExpenditureDetailsResponseDto;
import com.wanted.safewallet.domain.expenditure.web.dto.response.ExpenditureListByDateResponseDto;
import com.wanted.safewallet.domain.expenditure.web.dto.response.ExpenditureListByDateResponseDto.ExpenditureResponseDto;
import com.wanted.safewallet.domain.expenditure.web.dto.response.ExpenditureListResponseDto;
import com.wanted.safewallet.domain.expenditure.web.dto.response.ExpenditureExceptsResponseDto;
import com.wanted.safewallet.domain.expenditure.web.dto.response.TotalAmountByCategoryResponseDto;
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
            .note(expenditure.getNote()).build();
    }

    public ExpenditureListResponseDto toListDto(long totalAmount,
        List<StatsByCategoryResponseDto> statsByCategory, Page<Expenditure> expenditurePage) {
        Map<LocalDate, List<Expenditure>> expenditureListByDate = expenditurePage.getContent().stream()
            .collect(groupingBy(Expenditure::getExpenditureDate, toList()));
        return ExpenditureListResponseDto.builder()
            .totalAmount(totalAmount)
            .totalAmountListByCategory(toListByCategoryDto(statsByCategory))
            .expenditureListByDate(toListByDateDto(expenditureListByDate))
            .paging(new PageResponse(expenditurePage)).build();
    }

    public ExpenditureExceptsResponseDto toExceptsDto(long totalAmount,
        List<StatsByCategoryResponseDto> statsByCategory) {
        return ExpenditureExceptsResponseDto.builder()
            .totalAmount(totalAmount)
            .totalAmountListByCategory(toListByCategoryDto(statsByCategory))
            .build();
    }

    private List<TotalAmountByCategoryResponseDto> toListByCategoryDto(
        List<StatsByCategoryResponseDto> statsByCategory) {
        return statsByCategory.stream()
            .sorted(Comparator.comparing(StatsByCategoryResponseDto::getCategoryId))
            .map(stats -> TotalAmountByCategoryResponseDto.builder()
                .categoryId(stats.getCategoryId())
                .type(stats.getType())
                .totalAmount(stats.getTotalAmount())
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
                .note(expenditure.getNote())
                .build())
            .toList();
    }
}
