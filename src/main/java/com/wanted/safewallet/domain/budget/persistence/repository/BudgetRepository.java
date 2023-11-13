package com.wanted.safewallet.domain.budget.persistence.repository;

import com.wanted.safewallet.domain.budget.persistence.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BudgetRepository extends JpaRepository<Budget, Long> {

}
