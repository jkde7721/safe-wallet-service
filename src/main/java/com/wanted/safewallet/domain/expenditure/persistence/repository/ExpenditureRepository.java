package com.wanted.safewallet.domain.expenditure.persistence.repository;

import com.wanted.safewallet.domain.expenditure.persistence.entity.Expenditure;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenditureRepository extends JpaRepository<Expenditure, Long> {

}
