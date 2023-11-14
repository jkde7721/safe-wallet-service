package com.wanted.safewallet.domain.budget.persistence.repository;

import static com.wanted.safewallet.domain.budget.persistence.entity.QBudget.budget;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.YearMonth;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BudgetRepositoryImpl implements BudgetRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public boolean existsByUserIdAndBudgetYearMonthAndInCategories(String userId,
        YearMonth budgetYearMonth, List<Long> categoryIds) {
        return Boolean.TRUE.equals(queryFactory
            .select(budgetCountCase())
            .from(budget)
            .where(budget.user.id.eq(userId),
                budget.budgetYearMonth.eq(budgetYearMonth),
                budget.category.id.in(categoryIds))
            .fetchOne());
    }

    private BooleanExpression budgetCountCase() {
        return new CaseBuilder()
            .when(budget.count().gt(0)).then(true)
            .otherwise(false);
    }
}
