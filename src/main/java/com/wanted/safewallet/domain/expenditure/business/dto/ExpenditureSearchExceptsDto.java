package com.wanted.safewallet.domain.expenditure.business.dto;

import com.wanted.safewallet.domain.category.persistence.entity.Category;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ExpenditureSearchExceptsDto {

    private long totalAmount;

    private Map<Category, Long> expenditureAmountByCategory;
}
