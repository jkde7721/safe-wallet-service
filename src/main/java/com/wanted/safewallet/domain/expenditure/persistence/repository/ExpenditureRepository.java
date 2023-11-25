package com.wanted.safewallet.domain.expenditure.persistence.repository;

import com.wanted.safewallet.domain.expenditure.persistence.entity.Expenditure;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ExpenditureRepository extends JpaRepository<Expenditure, Long> {

    @Query("select e from Expenditure e where e.id = :expenditureId and e.deleted = false")
    Optional<Expenditure> findById(@Param("expenditureId") Long expenditureId);

    @Query("select e from Expenditure e join fetch e.category where e.id = :expenditureId and e.deleted = false")
    Optional<Expenditure> findByIdFetch(@Param("expenditureId") Long expenditureId);
}
