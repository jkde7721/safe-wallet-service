package com.wanted.safewallet.domain.expenditure.business.dto;

import com.wanted.safewallet.domain.category.persistence.entity.Category;
import com.wanted.safewallet.domain.expenditure.persistence.entity.Expenditure;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
@Builder
@AllArgsConstructor
public class ExpenditureSearchDto {

    private long totalAmount;

    private Map<Category, Long> expenditureAmountByCategory;

    private Page<Expenditure> expenditurePage;
}
