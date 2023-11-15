package com.wanted.safewallet.domain.expenditure.web.dto.request;

import com.wanted.safewallet.domain.category.persistence.entity.CategoryType;
import com.wanted.safewallet.global.dto.request.format.CustomLocalDateFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ExpenditureUpdateRequestDto {

    @CustomLocalDateFormat
    @NotNull(message = "{expenditure.update.notNull}")
    private LocalDate expenditureDate;

    @Min(value = 0, message = "{expenditure.update.min.zero}")
    @NotNull(message = "{expenditure.update.notNull}")
    private Long amount;

    @NotNull(message = "{expenditure.update.notNull}")
    private Long categoryId;

    @NotNull(message = "{expenditure.update.notNull}")
    private CategoryType type;

    private String note = "";
}
