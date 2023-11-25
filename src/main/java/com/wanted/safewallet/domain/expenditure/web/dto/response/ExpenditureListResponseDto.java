package com.wanted.safewallet.domain.expenditure.web.dto.response;

import com.wanted.safewallet.domain.category.persistence.entity.CategoryType;
import com.wanted.safewallet.global.dto.response.PageResponse;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ExpenditureListResponseDto {

    private Long totalAmount;

    private List<TotalAmountByCategoryResponseDto> totalAmountListByCategory;

    private List<ExpenditureListByDateResponseDto> expenditureListByDate;

    private PageResponse paging;

    @Getter
    @Builder
    @AllArgsConstructor
    public static class TotalAmountByCategoryResponseDto {

        private Long categoryId;

        private CategoryType type;

        private Long totalAmount;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class ExpenditureListByDateResponseDto {

        private LocalDate expenditureDate;

        private List<ExpenditureResponseDto> expenditureList;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class ExpenditureResponseDto {

        private Long expenditureId;

        private Long amount;

        private Long categoryId;

        private CategoryType type;

        private String note;
    }
}
