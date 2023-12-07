package com.wanted.safewallet.domain.expenditure.persistence.repository;

import static com.querydsl.core.types.Projections.constructor;
import static com.wanted.safewallet.domain.category.persistence.entity.QCategory.category;
import static com.wanted.safewallet.domain.expenditure.persistence.entity.QExpenditure.expenditure;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wanted.safewallet.domain.expenditure.persistence.dto.response.StatsByCategoryResponseDto;
import com.wanted.safewallet.domain.expenditure.persistence.dto.response.TotalAmountByCategoryResponseDto;
import com.wanted.safewallet.domain.expenditure.persistence.entity.Expenditure;
import com.wanted.safewallet.domain.expenditure.web.dto.request.ExpenditureSearchCond;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.CollectionUtils;

@RequiredArgsConstructor
public class ExpenditureRepositoryImpl implements ExpenditureRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public long getTotalAmount(String userId, ExpenditureSearchCond searchCond) {
        return Optional.ofNullable(queryFactory.select(expenditure.amount.sum())
                .from(expenditure)
                .where(expenditureSearchExpression(userId, searchCond),
                    expenditureIdNotIn(searchCond.getExcepts()))
                .fetchOne())
            .orElse(0L);
    }

    @Override
    public List<StatsByCategoryResponseDto> getStatsByCategory(String userId, ExpenditureSearchCond searchCond) {
        return queryFactory.select(
            constructor(StatsByCategoryResponseDto.class,
                category.id,
                category.type,
                expenditure.amount.sum()
            ))
            .from(expenditure)
            .where(expenditureSearchExpression(userId, searchCond),
                expenditureIdNotIn(searchCond.getExcepts()))
            .join(expenditure.category, category)
            .groupBy(category.id)
            .fetch();
    }

    @Override
    public Page<Expenditure> findAllFetch(String userId, ExpenditureSearchCond searchCond, Pageable pageable) {
        List<Expenditure> content = queryFactory.selectFrom(expenditure)
            .where(expenditureSearchExpression(userId, searchCond))
            .join(expenditure.category).fetchJoin()
            .orderBy(expenditure.expenditureDate.desc(), expenditure.amount.desc(), expenditure.id.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        JPAQuery<Long> countQuery = queryFactory.select(expenditure.count())
            .from(expenditure)
            .where(expenditureSearchExpression(userId, searchCond));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    @Override
    public List<TotalAmountByCategoryResponseDto> getTotalAmountByCategoryList(String userId,
        LocalDate startDate, LocalDate endDate) {
        return queryFactory
            .select(constructor(TotalAmountByCategoryResponseDto.class,
                category, expenditure.amount.coalesce(0L).sum()))
            .from(expenditure)
            .rightJoin(expenditure.category, category)
            .on(userIdEq(userId), expenditureDateBetween(startDate, endDate), notDeleted())
            .groupBy(category.id)
            .fetch();
    }

    private BooleanExpression notDeleted() {
        return expenditure.deleted.isFalse();
    }

    private BooleanExpression expenditureSearchExpression(String userId, ExpenditureSearchCond searchCond) {
        return userIdEq(userId)
            .and(expenditureDateBetween(searchCond.getStartDate(), searchCond.getEndDate()))
            .and(categoryIdIn(searchCond.getCategories()))
            .and(amountBetween(searchCond.getMinAmount(), searchCond.getMaxAmount()))
            .and(notDeleted());
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

    private BooleanExpression expenditureIdNotIn(List<Long> excepts) {
        return CollectionUtils.isEmpty(excepts) ? null : expenditure.id.notIn(excepts);
    }

    private BooleanExpression alwaysFalse() {
        return Expressions.asBoolean(true).isFalse();
    }
}
