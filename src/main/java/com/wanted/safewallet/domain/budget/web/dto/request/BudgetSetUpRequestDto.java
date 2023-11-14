package com.wanted.safewallet.domain.budget.web.dto.request;

import com.wanted.safewallet.domain.category.persistence.entity.CategoryType;
import com.wanted.safewallet.global.dto.request.format.CustomYearMonthFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.YearMonth;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BudgetSetUpRequestDto {

    @FutureOrPresent(message = "{budget.setup.futureOrPresent}")
    @NotNull(message = "{budget.setup.notNull}")
    @CustomYearMonthFormat
    private YearMonth budgetYearMonth;

    @Valid
    @NotEmpty(message = "{budget.setup.notEmpty}")
    private List<BudgetByCategory> budgetList;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BudgetByCategory {

        @NotNull(message = "{budget.setup.notNull}")
        private Long categoryId;

        @NotNull(message = "{budget.setup.notNull}")
        private CategoryType type; //TODO: Enum 매핑

        @NotNull(message = "{budget.setup.notNull}")
        private Long amount;
    }
}
