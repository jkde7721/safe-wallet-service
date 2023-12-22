package com.wanted.safewallet.domain.user.persistence.repository;

import com.wanted.safewallet.domain.user.persistence.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, String> {

    //Soft Delete 유저의 계정명도 조회 대상
    @Query("SELECT CASE WHEN COUNT(*) > 0 THEN true ELSE false END FROM User u WHERE u.username = :username")
    boolean existsByUsername(@Param("username") String username);

    @Query("select u from User u where u.id = :userId and u.deleted = false")
    Optional<User> findById(@Param("userId") String userId);

    @Query("select u from User u where u.username = :username and u.deleted = true")
    Optional<User> findInactiveUserByUsername(@Param("username") String username);

    @Query("select u from User u where u.username = :username and u.deleted = false")
    Optional<User> findByUsername(@Param("username") String username);
}
