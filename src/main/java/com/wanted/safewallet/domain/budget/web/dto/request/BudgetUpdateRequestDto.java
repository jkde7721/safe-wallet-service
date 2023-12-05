package com.wanted.safewallet.domain.budget.web.dto.request;

import com.wanted.safewallet.domain.category.persistence.entity.CategoryType;
import com.wanted.safewallet.global.dto.request.format.CustomYearMonthFormat;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.YearMonth;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BudgetUpdateRequestDto {

    @NotNull(message = "{budget.update.notNull}")
    @CustomYearMonthFormat
    private YearMonth budgetYearMonth;

    @NotNull(message = "{budget.update.notNull}")
    private Long categoryId;

    @NotNull(message = "{budget.update.notNull}")
    private CategoryType type;

    @Min(value = 0, message = "{budget.update.min}")
    @Max(value = 100_000_000, message = "{budget.update.max}")
    @NotNull(message = "{budget.update.notNull}")
    private Long amount;
}
