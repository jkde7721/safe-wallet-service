package com.wanted.safewallet.domain.budget.persistence.repository;

import com.wanted.safewallet.domain.budget.persistence.entity.Budget;
import java.time.YearMonth;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BudgetRepository extends JpaRepository<Budget, Long>, BudgetRepositoryCustom {

    @Query("select b from Budget b join fetch b.category where b.user.id = :userId and b.category.id = :categoryId and b.budgetYearMonth = :budgetYearMonth")
    Optional<Budget> findByUserAndCategoryAndBudgetYearMonthFetch(@Param("userId") String userId,
        @Param("categoryId") Long categoryId, @Param("budgetYearMonth") YearMonth budgetYearMonth);
}
