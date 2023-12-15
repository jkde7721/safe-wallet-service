package com.wanted.safewallet.domain.auth.business.dto;

import com.wanted.safewallet.domain.user.persistence.entity.User;
import java.util.List;
import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class CustomUserDetails extends org.springframework.security.core.userdetails.User {

    @Getter
    private final String userId;

    public CustomUserDetails(User user) {
        super(user.getUsername(), user.getPassword(), List.of(new SimpleGrantedAuthority("ROLE_USER")));
        this.userId = user.getId();
    }
}
