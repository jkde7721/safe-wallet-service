package com.wanted.safewallet.domain.auth.handler;

import com.wanted.safewallet.domain.auth.business.service.RefreshTokenService;
import com.wanted.safewallet.domain.auth.utils.CookieUtils;
import com.wanted.safewallet.domain.auth.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@RequiredArgsConstructor
@Component
public class JwtLogoutSuccessHandler implements LogoutSuccessHandler {

    private final JwtUtils jwtUtils;
    private final CookieUtils cookieUtils;
    private final RefreshTokenService refreshTokenService;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) throws IOException {
        String token = cookieUtils.getToken(request);
        if (StringUtils.hasText(token) && jwtUtils.validateToken(token)) {
            refreshTokenService.deleteToken(token);
            cookieUtils.deleteToken(request, response);
        }
        response.getWriter().flush();
    }
}
