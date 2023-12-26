package com.wanted.safewallet.utils;

import static com.wanted.safewallet.domain.user.persistence.entity.Role.USER;

import com.wanted.safewallet.domain.budget.persistence.entity.Budget;
import com.wanted.safewallet.domain.budget.persistence.entity.Budget.BudgetBuilder;
import com.wanted.safewallet.domain.category.persistence.entity.Category;
import com.wanted.safewallet.domain.category.persistence.entity.Category.CategoryBuilder;
import com.wanted.safewallet.domain.category.persistence.entity.CategoryType;
import com.wanted.safewallet.domain.expenditure.persistence.entity.Expenditure;
import com.wanted.safewallet.domain.expenditure.persistence.entity.Expenditure.ExpenditureBuilder;
import com.wanted.safewallet.domain.user.persistence.entity.User;
import com.wanted.safewallet.domain.user.persistence.entity.User.UserBuilder;
import java.time.LocalDateTime;
import java.time.YearMonth;

public abstract class Fixtures {

    public static BudgetBuilder aBudget() {
        return Budget.builder()
            .id(1L)
            .user(anUser().build())
            .category(aCategory().build())
            .budgetYearMonth(YearMonth.now())
            .amount(10000L);
    }

    public static CategoryBuilder aCategory() {
        return Category.builder()
            .id(1L)
            .type(CategoryType.FOOD);
    }

    public static ExpenditureBuilder anExpenditure() {
        return Expenditure.builder()
            .id(1L)
            .user(anUser().build())
            .category(aCategory().build())
            .expenditureDate(LocalDateTime.now())
            .amount(10000L)
            .title("편의점 점심")
            .note("삼각김밥 & 컵라면");
    }

    public static UserBuilder anUser() {
        return User.builder()
            .id("userId")
            .username("username@naver.com")
            .password("{bcrypt}password")
            .role(USER);
    }
}
