package com.wanted.safewallet.domain.expenditure.web.dto.request;

import com.wanted.safewallet.domain.category.persistence.entity.CategoryType;
import com.wanted.safewallet.global.dto.request.format.CustomLocalDateTimeFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ExpenditureCreateRequestDto {

    @CustomLocalDateTimeFormat
    @NotNull(message = "{expenditure.create.notNull}")
    private LocalDateTime expenditureDate;

    @Min(value = 0, message = "{expenditure.create.min.zero}")
    @NotNull(message = "{expenditure.create.notNull}")
    private Long amount;

    @NotNull(message = "{expenditure.create.notNull}")
    private Long categoryId;

    @NotNull(message = "{expenditure.create.notNull}")
    private CategoryType type;

    private String note = "";
}
