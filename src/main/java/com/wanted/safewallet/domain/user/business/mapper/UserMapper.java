package com.wanted.safewallet.domain.user.business.mapper;

import com.wanted.safewallet.domain.user.web.dto.response.UsernameCheckResponse;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UsernameCheckResponse toResponse(boolean isDuplicatedUsername) {
        return new UsernameCheckResponse(isDuplicatedUsername);
    }
}
