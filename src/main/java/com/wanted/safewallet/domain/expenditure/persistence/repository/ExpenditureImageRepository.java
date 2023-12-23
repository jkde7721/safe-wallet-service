package com.wanted.safewallet.domain.expenditure.persistence.repository;

import com.wanted.safewallet.domain.expenditure.persistence.entity.ExpenditureImage;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ExpenditureImageRepository extends JpaRepository<ExpenditureImage, Long> {

    @Modifying
    @Query("delete from ExpenditureImage ei where ei.expenditure.id in :expenditureIds")
    void deleteAllByExpenditureIn(@Param("expenditureIds") List<Long> expenditureIds);

    @Modifying
    @Query("delete from ExpenditureImage ei where ei.expenditure.id = :expenditureId")
    void deleteAllByExpenditure(@Param("expenditureId") Long expenditureId);
}
