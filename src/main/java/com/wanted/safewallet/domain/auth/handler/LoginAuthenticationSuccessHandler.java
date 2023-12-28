package com.wanted.safewallet.domain.auth.handler;

import com.wanted.safewallet.domain.auth.business.dto.CustomUserDetails;
import com.wanted.safewallet.domain.auth.business.service.RefreshTokenService;
import com.wanted.safewallet.domain.auth.utils.CookieUtils;
import com.wanted.safewallet.domain.auth.utils.HeaderUtils;
import com.wanted.safewallet.domain.auth.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@RequiredArgsConstructor
@Component
public class LoginAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtils jwtUtils;
    private final HeaderUtils headerUtils;
    private final CookieUtils cookieUtils;
    private final RefreshTokenService refreshTokenService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String authorities = getCommaDelimitedAuthorities(userDetails.getAuthorities());
        String accessToken = jwtUtils.generateAccessToken(userDetails.getUsername(),
            userDetails.getUserId(), authorities);
        String refreshToken = jwtUtils.generateRefreshToken(userDetails.getUsername());

        refreshTokenService.saveToken(refreshToken);
        headerUtils.setToken(response, accessToken);
        cookieUtils.setToken(response, refreshToken);
    }

    private String getCommaDelimitedAuthorities(Collection<GrantedAuthority> authorities) {
        return StringUtils.collectionToCommaDelimitedString(authorities);
    }
}
