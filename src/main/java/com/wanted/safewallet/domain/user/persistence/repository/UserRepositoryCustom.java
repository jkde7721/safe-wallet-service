package com.wanted.safewallet.domain.user.persistence.repository;

import java.time.LocalDateTime;
import java.util.List;

public interface UserRepositoryCustom {

    List<String> findIdsByDeletedAndDeletedDate(LocalDateTime minDeletedDate);
}
