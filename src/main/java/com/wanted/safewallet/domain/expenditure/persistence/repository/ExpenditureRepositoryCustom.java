package com.wanted.safewallet.domain.expenditure.persistence.repository;

import com.wanted.safewallet.domain.category.persistence.entity.Category;
import com.wanted.safewallet.domain.expenditure.persistence.dto.response.StatsByCategoryResponseDto;
import com.wanted.safewallet.domain.expenditure.persistence.dto.response.TotalAmountByCategoryResponseDto;
import com.wanted.safewallet.domain.expenditure.persistence.entity.Expenditure;
import com.wanted.safewallet.domain.expenditure.web.dto.request.ExpenditureSearchCond;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ExpenditureRepositoryCustom {

    long getTotalAmount(String userId, ExpenditureSearchCond searchCond);

    List<StatsByCategoryResponseDto> getStatsByCategory(String userId, ExpenditureSearchCond searchCond);

    Page<Expenditure> findAllFetch(String userId, ExpenditureSearchCond searchCond, Pageable pageable);

    List<TotalAmountByCategoryResponseDto> getTotalAmountByCategoryList(String userId, LocalDate startDate, LocalDate endDate);

    Map<Category, Long> findTotalAmountMapByUserAndExpenditureDateRange(String userId, LocalDate startDate, LocalDate endDate);
}
