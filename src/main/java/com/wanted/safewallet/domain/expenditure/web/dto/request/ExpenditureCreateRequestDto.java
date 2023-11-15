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
    @NotNull(message = "{expenditure.create.notNull}")
    private LocalDate expenditureDate;

    @Min(value = 0, message = "{expenditure.create.min.zero}")
    @NotNull(message = "{expenditure.create.notNull}")
    private Long amount;

    @NotNull(message = "{expenditure.create.notNull}")
    private Long categoryId;

    @NotNull(message = "{expenditure.create.notNull}")
    private CategoryType type;

    private String note = "";
}
