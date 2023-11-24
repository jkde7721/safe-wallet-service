package com.wanted.safewallet.domain.auth.persistence.repository;

import com.wanted.safewallet.domain.auth.persistence.entity.RefreshToken;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {

    Optional<RefreshToken> findByToken(String token);
}
