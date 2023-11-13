package com.wanted.safewallet.domain.user.persistence.repository;

import com.wanted.safewallet.domain.user.persistence.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {

}
