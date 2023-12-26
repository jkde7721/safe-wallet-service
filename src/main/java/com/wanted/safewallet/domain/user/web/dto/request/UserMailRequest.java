package com.wanted.safewallet.domain.user.web.dto.request;

import com.wanted.safewallet.domain.user.web.validation.ValidEmail;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserMailRequest {

    @ValidEmail
    @Email(message = "{user.username.email}")
    @NotBlank(message = "{user.username.notBlank}")
    private String email;
}
