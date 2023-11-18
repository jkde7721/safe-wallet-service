package com.wanted.safewallet.domain.user.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UsernameCheckResponseDto {

    Boolean isDuplicatedUsername;
}
