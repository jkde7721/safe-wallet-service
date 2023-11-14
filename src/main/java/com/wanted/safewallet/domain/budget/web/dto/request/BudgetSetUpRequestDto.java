package com.wanted.safewallet.domain.budget.web.dto.request;

import com.wanted.safewallet.domain.category.persistence.entity.CategoryType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.YearMonth;
import java.util.List;
import lombok.Getter;

@Getter
public class BudgetSetUpRequestDto {

    @FutureOrPresent
    @NotNull
    private YearMonth budgetYearMonth; //TODO: 날짜 매핑

    @Valid
    @NotEmpty
    private List<BudgetByCategory> budgetList;

    @Getter
    public static class BudgetByCategory {

        @NotNull
        private Long categoryId;

        @NotNull
        private CategoryType type; //TODO: Enum 매핑

        @NotNull
        private Long amount;
    }
}
