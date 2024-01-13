package com.wanted.safewallet.domain.user.web.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.List;
import org.springframework.util.StringUtils;

public class EmailConstraintValidator implements ConstraintValidator<ValidEmail, String> {

    private static final List<String> emailProviders = List.of(
        "naver.com", "daum.net", "hanmail.net", "gmail.com", "hotmail.com", "nate.com");

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if (!StringUtils.hasText(email)) {
            return false;
        }
        for (String provider : emailProviders) {
            if (email.endsWith(provider)) {
                return true;
            }
        }
        return false;
    }
}
