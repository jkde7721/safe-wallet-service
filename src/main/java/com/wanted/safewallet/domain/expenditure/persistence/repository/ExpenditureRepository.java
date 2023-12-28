package com.wanted.safewallet.domain.expenditure.persistence.repository;

import com.wanted.safewallet.domain.expenditure.persistence.entity.Expenditure;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ExpenditureRepository extends JpaRepository<Expenditure, Long>, ExpenditureRepositoryCustom {

    @Query("select e.id from Expenditure e where e.user.id in :userIds")
    List<Long> findIdsByUser(@Param("userIds") List<String> userIds);

    @Modifying
    @Query("delete from Expenditure e where e.user.id in :userIds")
    void deleteAllByUserIn(@Param("userIds") List<String> userIds);
}
