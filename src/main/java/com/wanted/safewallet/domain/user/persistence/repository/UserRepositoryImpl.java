package com.wanted.safewallet.domain.user.persistence.repository;

import static com.wanted.safewallet.domain.user.persistence.entity.QUser.user;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<String> findIdsByDeletedAndDeletedDate(LocalDateTime minDeletedDate) {
        return queryFactory.select(user.id)
            .from(user)
            .where(user.deleted.isTrue(),
                user.deletedDate.before(minDeletedDate))
            .fetch();
    }
}
