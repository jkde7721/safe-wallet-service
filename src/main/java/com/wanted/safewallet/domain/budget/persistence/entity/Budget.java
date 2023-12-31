package com.wanted.safewallet.domain.budget.persistence.entity;

import com.wanted.safewallet.domain.budget.persistence.converter.YearMonthAttributeConverter;
import com.wanted.safewallet.domain.category.persistence.entity.Category;
import com.wanted.safewallet.domain.category.persistence.entity.CategoryType;
import com.wanted.safewallet.domain.user.persistence.entity.User;
import com.wanted.safewallet.global.audit.BaseTime;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.YearMonth;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Budget extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "budget_id", updatable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false)
    private Long amount;

    @Convert(converter = YearMonthAttributeConverter.class)
    @Column(nullable = false)
    private YearMonth budgetYearMonth;

    public void update(Long categoryId, CategoryType type, Long amount, YearMonth budgetYearMonth) {
        this.category = Category.builder().id(categoryId).type(type).build();
        this.amount = amount;
        this.budgetYearMonth = budgetYearMonth;
    }

    public void addAmount(Long amount) {
        this.amount += amount;
    }
}
