package com.wanted.safewallet.domain.user.persistence.entity;

import com.wanted.safewallet.global.audit.BaseTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.UuidGenerator;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class User extends BaseTime {

    @Id
    @UuidGenerator
    @Column(name = "user_id", updatable = false)
    private String id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Builder.Default
    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.ANONYMOUS;

    @Builder.Default
    @Column(nullable = false)
    @ColumnDefault("0")
    private Boolean deleted = Boolean.FALSE;

    private LocalDateTime deletedDate;

    public String getAuthorities() {
        return this.role.getAuthorities();
    }

    public void updateRole(Role role) {
        this.role = role;
    }

    public void softDelete() {
        this.deleted = Boolean.TRUE;
        this.deletedDate = LocalDateTime.now();
    }

    public void restore() {
        this.deleted = Boolean.FALSE;
        this.deletedDate = null;
    }
}
