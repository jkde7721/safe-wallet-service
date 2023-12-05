package com.wanted.safewallet.domain.budget.web.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BudgetConsultRequestDto {

    @Min(value = 0, message = "{budget.consult.min}")
    @Max(value = 100_000_000, message = "{budget.consult.max}")
    @NotNull(message = "{budget.consult.notNull}")
    private Long totalAmount;
}
