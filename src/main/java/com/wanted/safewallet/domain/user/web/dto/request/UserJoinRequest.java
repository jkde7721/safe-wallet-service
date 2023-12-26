package com.wanted.safewallet.domain.user.web.dto.request;

import com.wanted.safewallet.domain.user.web.validation.ValidEmail;
import com.wanted.safewallet.domain.user.web.validation.ValidPassword;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ValidPassword
public class UserJoinRequest {

    @ValidEmail
    private String username;

    private String password;
}
