package com.wanted.safewallet.domain.auth.business.service;

import static com.wanted.safewallet.global.exception.ErrorCode.UNAUTHORIZED_JWT_TOKEN;

import com.wanted.safewallet.domain.auth.utils.JwtUtils;
import com.wanted.safewallet.domain.auth.business.dto.JwtDto;
import com.wanted.safewallet.domain.user.business.service.UserService;
import com.wanted.safewallet.domain.user.persistence.entity.User;
import com.wanted.safewallet.global.exception.BusinessException;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final JwtUtils jwtTokenUtils;
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;

    public JwtDto reissueToken(String accessToken, String refreshToken) {
        validateTokens(accessToken, refreshToken);

        String username = jwtTokenUtils.getUsername(refreshToken);
        User user = userService.getUserByUsername(username);
        String authorities = getCommaDelimitedAuthorities();

        String newAccessToken = jwtTokenUtils.generateAccessToken(username, user.getId(), authorities);
        String newRefreshToken = jwtTokenUtils.generateRefreshToken(username);
        refreshTokenService.replaceToken(refreshToken, newRefreshToken);
        return new JwtDto(newAccessToken, newRefreshToken);
    }

    private void validateTokens(String accessToken, String refreshToken) {
        if (!validateTokensItself(accessToken, refreshToken) ||
            !refreshTokenService.validateToken(refreshToken)) {
            throw new BusinessException(UNAUTHORIZED_JWT_TOKEN);
        }
    }

    private boolean validateTokensItself(String accessToken, String refreshToken) {
        return jwtTokenUtils.validateExpiredToken(accessToken) &&
            jwtTokenUtils.validateToken(refreshToken) &&
            Objects.equals(jwtTokenUtils.getUsername(accessToken), jwtTokenUtils.getUsername(refreshToken));
    }

    private String getCommaDelimitedAuthorities() {
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        return StringUtils.collectionToCommaDelimitedString(authorities);
    }
}
