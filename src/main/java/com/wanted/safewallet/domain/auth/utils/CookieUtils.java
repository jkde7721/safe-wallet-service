package com.wanted.safewallet.domain.auth.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CookieUtils {

    private static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";
    private static final String REFRESH_TOKEN_COOKIE_PATH = "/api/auth/refresh";
    private final JwtProperties jwtProperties;

    public String getToken(HttpServletRequest request) {
        Cookie cookie = Arrays.stream(getCookies(request))
            .filter(c -> c.getName().equals(REFRESH_TOKEN_COOKIE_NAME))
            .findFirst()
            .orElse(new Cookie(REFRESH_TOKEN_COOKIE_NAME, null));
        return cookie.getValue();
    }

    public void setToken(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, token);
        cookie.setHttpOnly(true);
        cookie.setMaxAge((int) jwtProperties.refreshTokenExpirySec());
        cookie.setPath(REFRESH_TOKEN_COOKIE_PATH);
        cookie.setSecure(false);
        response.addCookie(cookie);
    }

    //HttpServletRequest의 getCookies 메소드가 null을 반환할 수 있기 때문
    private Cookie[] getCookies(HttpServletRequest request) {
        return Optional.ofNullable(request.getCookies()).orElse(new Cookie[]{});
    }
}
