package com.wanted.safewallet.domain.expenditure.business.dto;

import com.wanted.safewallet.domain.category.persistence.entity.Category;
import com.wanted.safewallet.domain.expenditure.business.enums.FinanceStatus;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class TodayExpenditureTotalConsultDto {

    private long totalAmount;

    private FinanceStatus totalFinanceStatus;

    private Map<Category, TodayExpenditureConsultDto> todayExpenditureConsultByCategory;
}
