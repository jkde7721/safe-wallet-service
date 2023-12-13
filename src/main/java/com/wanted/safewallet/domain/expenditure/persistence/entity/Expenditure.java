package com.wanted.safewallet.domain.expenditure.persistence.entity;

import com.wanted.safewallet.domain.category.persistence.entity.Category;
import com.wanted.safewallet.domain.user.persistence.entity.User;
import com.wanted.safewallet.global.audit.BaseTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@DynamicUpdate
@DynamicInsert
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Expenditure extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "expenditure_id", updatable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false)
    private LocalDateTime expenditureDate;

    @Column(nullable = false)
    private Long amount;

    @Column(nullable = false, length = 500)
    private String note;

    @Builder.Default
    @Column(nullable = false)
    @ColumnDefault("0")
    private Boolean deleted = Boolean.FALSE;

    public void update(Long categoryId, LocalDateTime expenditureDate, Long amount, String note) {
        this.category = Category.builder().id(categoryId).build();
        this.expenditureDate = expenditureDate;
        this.amount = amount;
        this.note = note;
    }

    public void softDelete() {
        this.deleted = Boolean.TRUE;
    }
}
