package com.wanted.safewallet.domain.user.web.dto.request;

import com.wanted.safewallet.domain.user.web.validation.ValidEmail;
import com.wanted.safewallet.domain.user.web.validation.ValidPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ValidPassword
public class UserJoinRequest {

    @ValidEmail
    @Email(message = "{user.username.email}")
    @NotBlank(message = "{user.username.notBlank}")
    private String username;

    private String password;
}
