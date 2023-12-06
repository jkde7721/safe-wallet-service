package com.wanted.safewallet.domain.expenditure.persistence.dto.response;

import com.wanted.safewallet.domain.category.persistence.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TotalAmountByCategoryResponseDto {

    private Category category;

    private Long totalAmount;
}
