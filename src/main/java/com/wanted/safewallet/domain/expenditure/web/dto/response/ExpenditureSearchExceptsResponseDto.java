package com.wanted.safewallet.domain.expenditure.web.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ExpenditureSearchExceptsResponseDto {

    private Long totalAmount;

    private List<ExpenditureAmountOfCategoryResponseDto> expenditureAmountOfCategoryList;
}
