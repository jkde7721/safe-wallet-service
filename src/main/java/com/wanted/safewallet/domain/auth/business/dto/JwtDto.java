package com.wanted.safewallet.domain.auth.business.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JwtDto {

    private String accessToken;

    private String refreshToken;
}
