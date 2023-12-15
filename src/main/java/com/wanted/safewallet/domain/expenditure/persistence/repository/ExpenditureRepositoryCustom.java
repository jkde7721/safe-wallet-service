package com.wanted.safewallet.domain.expenditure.persistence.repository;

import com.wanted.safewallet.domain.expenditure.persistence.dto.response.ExpenditureAmountOfCategoryListResponseDto;
import com.wanted.safewallet.domain.expenditure.persistence.entity.Expenditure;
import com.wanted.safewallet.domain.expenditure.web.dto.request.ExpenditureSearchCond;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ExpenditureRepositoryCustom {

    Optional<Expenditure> findByIdFetch(Long expenditureId);

    long findTotalAmountByUserAndSearchCond(String userId, ExpenditureSearchCond searchCond);

    Page<Expenditure> findAllByUserAndSearchCondFetch(String userId, ExpenditureSearchCond searchCond, Pageable pageable);

    ExpenditureAmountOfCategoryListResponseDto findExpenditureAmountOfCategoryListByUserAndSearchCond(String userId, ExpenditureSearchCond searchCond);

    ExpenditureAmountOfCategoryListResponseDto findExpenditureAmountOfCategoryListByUserAndExpenditureDateBetween(String userId, LocalDateTime startInclusive, LocalDateTime endExclusive);
}
