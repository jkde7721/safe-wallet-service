package com.wanted.safewallet.domain.expenditure.persistence.repository;

import com.wanted.safewallet.domain.expenditure.business.dto.ExpenditureSearchCond;
import com.wanted.safewallet.domain.expenditure.persistence.dto.ExpenditureAmountOfCategoryListDto;
import com.wanted.safewallet.domain.expenditure.persistence.entity.Expenditure;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ExpenditureRepositoryCustom {

    Optional<Expenditure> findByIdFetch(Long expenditureId);

    long findTotalAmountByUserAndSearchCond(String userId, ExpenditureSearchCond searchCond);

    Page<Expenditure> findAllByUserAndSearchCondFetch(String userId, ExpenditureSearchCond searchCond, Pageable pageable);

    ExpenditureAmountOfCategoryListDto findExpenditureAmountOfCategoryListByUserAndSearchCond(String userId, ExpenditureSearchCond searchCond);

    ExpenditureAmountOfCategoryListDto findExpenditureAmountOfCategoryListByUserAndExpenditureDateBetween(String userId, LocalDateTime startInclusive, LocalDateTime endExclusive);
}
