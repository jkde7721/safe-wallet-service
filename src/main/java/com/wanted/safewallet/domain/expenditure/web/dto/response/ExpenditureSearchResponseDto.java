package com.wanted.safewallet.domain.expenditure.web.dto.response;

import com.wanted.safewallet.global.dto.response.PageResponse;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ExpenditureSearchResponseDto {

    private Long totalAmount;

    private List<ExpenditureAmountOfCategoryResponseDto> expenditureAmountOfCategoryList;

    private List<ExpenditureListByDateResponseDto> expenditureListByDate;

    private PageResponse paging;
}
