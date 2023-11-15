package com.wanted.safewallet.domain.expenditure.web.dto.request;

import com.wanted.safewallet.domain.category.persistence.entity.CategoryType;
import com.wanted.safewallet.global.dto.request.format.CustomLocalDateFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Getter;

@Getter
public class ExpenditureCreateRequestDto {

    @CustomLocalDateFormat
    @NotNull
    private LocalDate expenditureDate;

    @Min(0)
    @NotNull
    private Long amount;

    @NotNull
    private Long categoryId;

    @NotNull
    private CategoryType type;

    private String note = "";
}
