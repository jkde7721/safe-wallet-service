package com.wanted.safewallet.domain.user.web.dto.request;

import com.wanted.safewallet.domain.user.web.validation.ValidEmail;
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
    private String email;
}
