package com.wanted.safewallet.domain.expenditure.business.dto;

import com.wanted.safewallet.domain.category.persistence.entity.CategoryType;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ExpenditureUpdateDto {

    private LocalDateTime expenditureDate;

    private Long amount;

    private Long categoryId;

    private CategoryType type;

    private String title;

    private String note;
}
