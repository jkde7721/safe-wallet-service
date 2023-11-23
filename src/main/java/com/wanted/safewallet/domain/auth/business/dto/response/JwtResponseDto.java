package com.wanted.safewallet.domain.auth.business.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JwtResponseDto {

    private String accessToken;

    private String refreshToken;
}
