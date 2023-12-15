package com.wanted.safewallet.domain.expenditure.web.dto.request;

import com.wanted.safewallet.domain.category.persistence.entity.CategoryType;
import com.wanted.safewallet.global.dto.request.format.CustomLocalDateTimeFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ExpenditureUpdateRequest {

    @CustomLocalDateTimeFormat
    @NotNull(message = "{expenditure.update.notNull}")
    private LocalDateTime expenditureDate;

    @Min(value = 0, message = "{expenditure.update.min.zero}")
    @NotNull(message = "{expenditure.update.notNull}")
    private Long amount;

    @NotNull(message = "{expenditure.update.notNull}")
    private Long categoryId;

    @NotNull(message = "{expenditure.update.notNull}")
    private CategoryType type;

    @NotBlank(message = "{expenditure.update.notBlank}")
    private String title;

    private String note = "";
}
