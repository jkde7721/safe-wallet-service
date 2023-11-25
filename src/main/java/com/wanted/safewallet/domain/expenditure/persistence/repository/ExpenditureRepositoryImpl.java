package com.wanted.safewallet.domain.expenditure.persistence.repository;

import static com.querydsl.core.types.Projections.constructor;
import static com.wanted.safewallet.domain.category.persistence.entity.QCategory.category;
import static com.wanted.safewallet.domain.expenditure.persistence.entity.QExpenditure.expenditure;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wanted.safewallet.domain.expenditure.persistence.dto.response.StatsByCategoryResponseDto;
import com.wanted.safewallet.domain.expenditure.web.dto.request.ExpenditureSearchCond;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.util.CollectionUtils;

@RequiredArgsConstructor
public class ExpenditureRepositoryImpl implements ExpenditureRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<StatsByCategoryResponseDto> getStatsByCategory(String userId, ExpenditureSearchCond searchCond) {
        return queryFactory.select(
            constructor(StatsByCategoryResponseDto.class,
                category.id,
                category.type,
                expenditure.amount.sum()
            ))
            .from(expenditure)
            .where(expenditureSearchExpression(userId, searchCond))
            .join(expenditure.category, category)
            .groupBy(category.id)
            .fetch();
    }

    private BooleanExpression expenditureSearchExpression(String userId, ExpenditureSearchCond searchCond) {
        return userIdEq(userId)
            .and(expenditureDateBetween(searchCond.getStartDate(), searchCond.getEndDate()))
            .and(categoryIdIn(searchCond.getCategories()))
            .and(amountBetween(searchCond.getMinAmount(), searchCond.getMaxAmount()));
    }

    private BooleanExpression userIdEq(String userId) {
        return userId == null ? alwaysFalse() : expenditure.user.id.eq(userId);
    }

    private BooleanExpression expenditureDateBetween(LocalDate startDate, LocalDate endDate) {
        return expenditure.expenditureDate.between(startDate, endDate);
    }

    private BooleanExpression categoryIdIn(List<Long> categories) {
        return CollectionUtils.isEmpty(categories) ? null : expenditure.category.id.in(categories);
    }

    private BooleanExpression amountBetween(Long minAmount, Long maxAmount) {
        return expenditure.amount.between(minAmount, maxAmount);
    }

    private BooleanExpression alwaysFalse() {
        return Expressions.asBoolean(true).isFalse();
    }
}
