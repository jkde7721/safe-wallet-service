package com.wanted.safewallet.domain.expenditure.persistence.repository;

import com.wanted.safewallet.domain.expenditure.persistence.dto.response.StatsByCategoryResponseDto;
import com.wanted.safewallet.domain.expenditure.web.dto.request.ExpenditureSearchCond;
import java.util.List;

public interface ExpenditureRepositoryCustom {

    List<StatsByCategoryResponseDto> getStatsByCategory(String userId, ExpenditureSearchCond searchCond);
}
