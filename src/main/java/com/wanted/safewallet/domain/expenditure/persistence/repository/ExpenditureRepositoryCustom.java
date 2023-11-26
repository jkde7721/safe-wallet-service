package com.wanted.safewallet.domain.expenditure.persistence.repository;

import com.wanted.safewallet.domain.expenditure.persistence.dto.response.StatsByCategoryResponseDto;
import com.wanted.safewallet.domain.expenditure.persistence.entity.Expenditure;
import com.wanted.safewallet.domain.expenditure.web.dto.request.ExpenditureSearchCond;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ExpenditureRepositoryCustom {

    long getTotalAmount(String userId, ExpenditureSearchCond searchCond);

    List<StatsByCategoryResponseDto> getStatsByCategory(String userId, ExpenditureSearchCond searchCond);

    Page<Expenditure> findAllFetch(String userId, ExpenditureSearchCond searchCond, Pageable pageable);
}
