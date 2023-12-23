package com.wanted.safewallet.domain.expenditure.persistence.repository;

import static com.querydsl.core.types.Projections.constructor;
import static com.wanted.safewallet.domain.category.persistence.entity.QCategory.category;
import static com.wanted.safewallet.domain.expenditure.persistence.entity.QExpenditure.expenditure;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wanted.safewallet.domain.expenditure.business.dto.ExpenditureSearchCond;
import com.wanted.safewallet.domain.expenditure.persistence.dto.ExpenditureAmountOfCategoryListDto;
import com.wanted.safewallet.domain.expenditure.persistence.dto.ExpenditureAmountOfCategoryListDto.ExpenditureAmountOfCategoryDto;
import com.wanted.safewallet.domain.expenditure.persistence.entity.Expenditure;
import java.time.LocalDateTime;
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
    public Optional<Expenditure> findByIdFetch(Long expenditureId) {
        return Optional.ofNullable(queryFactory.selectFrom(expenditure)
            .join(expenditure.category).fetchJoin()
            .leftJoin(expenditure.images).fetchJoin()
            .where(expenditure.id.eq(expenditureId))
            .fetchOne());
    }

    @Override
    public long findTotalAmountByUserAndSearchCond(String userId, ExpenditureSearchCond searchCond) {
        return Optional.ofNullable(queryFactory.select(expenditure.amount.sum())
                .from(expenditure)
                .where(expenditureSearchExpression(userId, searchCond),
                    expenditureIdNotIn(searchCond.getExcepts()))
                .fetchOne())
            .orElse(0L);
    }

    @Override
    public Page<Expenditure> findAllByUserAndSearchCondFetch(String userId, ExpenditureSearchCond searchCond, Pageable pageable) {
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
    public ExpenditureAmountOfCategoryListDto findExpenditureAmountOfCategoryListByUserAndSearchCond(String userId, ExpenditureSearchCond searchCond) {
        return new ExpenditureAmountOfCategoryListDto(
            queryFactory.select(constructor(ExpenditureAmountOfCategoryDto.class,
                    category, expenditure.amount.coalesce(0L).sum()))
                .from(expenditure)
                .rightJoin(expenditure.category, category)
                .on(expenditureSearchExpression(userId, searchCond)
                    .and(expenditureIdNotIn(searchCond.getExcepts())))
                .groupBy(category.id)
                .fetch());
    }

    @Override
    public ExpenditureAmountOfCategoryListDto findExpenditureAmountOfCategoryListByUserAndExpenditureDateBetween(
        String userId, LocalDateTime startInclusive, LocalDateTime endExclusive) {
        return new ExpenditureAmountOfCategoryListDto(
            queryFactory.select(constructor(ExpenditureAmountOfCategoryDto.class,
                    category, expenditure.amount.coalesce(0L).sum()))
                .from(expenditure)
                .rightJoin(expenditure.category, category)
                .on(userIdEq(userId), expenditureDateBetween(startInclusive, endExclusive))
                .groupBy(category.id)
                .fetch());
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

    private BooleanExpression expenditureDateBetween(LocalDateTime startInclusive, LocalDateTime endExclusive) {
        return expenditure.expenditureDate.goe(startInclusive)
            .and(expenditure.expenditureDate.lt(endExclusive));
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
