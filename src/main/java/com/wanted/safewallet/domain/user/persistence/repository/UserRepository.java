package com.wanted.safewallet.domain.user.persistence.repository;

import com.wanted.safewallet.domain.user.persistence.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, String> {

    @Query("SELECT CASE WHEN COUNT(*) > 0 THEN true ELSE false END FROM User u WHERE u.username = :username")
    boolean existsByUsername(@Param("username") String username);

    Optional<User> findByUsername(String username);
}
