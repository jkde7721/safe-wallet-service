package com.wanted.safewallet.domain.budget.persistence.repository;

import static com.querydsl.core.types.Projections.constructor;
import static com.wanted.safewallet.domain.budget.persistence.entity.QBudget.budget;
import static com.wanted.safewallet.domain.category.persistence.entity.QCategory.category;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wanted.safewallet.domain.budget.persistence.dto.response.BudgetAmountOfCategoryListResponseDto;
import com.wanted.safewallet.domain.budget.persistence.dto.response.BudgetAmountOfCategoryListResponseDto.BudgetAmountOfCategoryResponseDto;
import java.time.YearMonth;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BudgetRepositoryImpl implements BudgetRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public boolean existsByUserAndBudgetYearMonthAndCategories(String userId,
        YearMonth budgetYearMonth, List<Long> categoryIds) {
        return Boolean.TRUE.equals(queryFactory
            .select(budgetCountCase())
            .from(budget)
            .where(budget.user.id.eq(userId),
                budget.budgetYearMonth.eq(budgetYearMonth),
                budget.category.id.in(categoryIds))
            .fetchOne());
    }

    @Override
    public BudgetAmountOfCategoryListResponseDto findBudgetAmountOfCategoryListByUserAndBudgetYearMonth(String userId, YearMonth budgetYearMonth) {
        return new BudgetAmountOfCategoryListResponseDto (
            queryFactory.select(constructor(BudgetAmountOfCategoryResponseDto.class,
                    category, budget.amount.coalesce(0L).sum()))
                .from(budget)
                .rightJoin(budget.category, category)
                .on(userIdEq(userId), budgetYearMonthEq(budgetYearMonth))
                .groupBy(category.id)
                .fetch());
    }

    private BooleanExpression budgetCountCase() {
        return new CaseBuilder()
            .when(budget.count().gt(0)).then(true)
            .otherwise(false);
    }

    private BooleanExpression userIdEq(String userId) {
        return userId == null ? alwaysTrue() : budget.user.id.eq(userId);
    }

    private BooleanExpression budgetYearMonthEq(YearMonth budgetYearMonth) {
        return budgetYearMonth == null ? alwaysTrue() : budget.budgetYearMonth.eq(budgetYearMonth);
    }

    private BooleanExpression alwaysTrue() {
        return Expressions.asBoolean(true).isTrue();
    }
}
