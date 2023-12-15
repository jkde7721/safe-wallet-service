package com.wanted.safewallet.domain.user.web.dto.request;

import com.wanted.safewallet.domain.user.web.validation.ValidPassword;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ValidPassword
public class UserJoinRequest {

    @NotBlank(message = "{user.join.username}")
    private String username;

    private String password;
}
