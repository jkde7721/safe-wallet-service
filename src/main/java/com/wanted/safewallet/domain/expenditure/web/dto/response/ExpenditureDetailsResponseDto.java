package com.wanted.safewallet.domain.expenditure.web.dto.response;

import com.wanted.safewallet.domain.category.persistence.entity.CategoryType;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ExpenditureDetailsResponseDto {

    private LocalDate expenditureDate;

    private Long amount;

    private Long categoryId;

    private CategoryType type;

    private String note;
}
