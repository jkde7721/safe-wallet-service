package com.wanted.safewallet.domain.expenditure.web.dto.response;

import com.wanted.safewallet.domain.category.persistence.entity.CategoryType;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ExpenditureListByDateResponse {

    private LocalDate expenditureDate;

    private List<ExpenditureResponse> expenditureList;

    @Getter
    @Builder
    @AllArgsConstructor
    public static class ExpenditureResponse {

        private Long expenditureId;

        private Long amount;

        private Long categoryId;

        private CategoryType type;

        private String title;
    }
}
