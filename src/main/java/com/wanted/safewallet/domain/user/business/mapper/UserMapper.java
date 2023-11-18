package com.wanted.safewallet.domain.user.business.mapper;

import com.wanted.safewallet.domain.user.web.dto.response.UsernameCheckResponseDto;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UsernameCheckResponseDto toDto(boolean isDuplicatedUsername) {
        return new UsernameCheckResponseDto(isDuplicatedUsername);
    }
}
