package com.wanted.safewallet.domain.user.business.mapper;

import static com.wanted.safewallet.domain.user.persistence.entity.Role.ANONYMOUS;

import com.wanted.safewallet.domain.user.persistence.entity.User;
import com.wanted.safewallet.domain.user.web.dto.request.UserJoinRequest;
import com.wanted.safewallet.domain.user.web.dto.response.UsernameCheckResponse;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toEntity(UserJoinRequest request, String encodedPassword) {
        return User.builder()
            .username(request.getUsername())
            .password(encodedPassword)
            .role(ANONYMOUS).build();
    }

    public UsernameCheckResponse toResponse(boolean isDuplicatedUsername) {
        return new UsernameCheckResponse(isDuplicatedUsername);
    }
}
