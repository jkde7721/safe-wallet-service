package com.wanted.safewallet.domain.auth.utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@RequiredArgsConstructor
@Component
public class HeaderUtils {

    private static final String AUTHORIZATION_HEADER_NAME = "Authorization";
    private final JwtProperties jwtProperties;

    public String getToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER_NAME);
        String bearerTokenPrefix = jwtProperties.prefix();
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(bearerTokenPrefix)) {
            return bearerToken.substring(bearerTokenPrefix.length());
        }
        return null;
    }

    public void setToken(HttpServletResponse response, String token) {
        response.setHeader(AUTHORIZATION_HEADER_NAME, token);
    }
}
