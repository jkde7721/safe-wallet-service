package com.wanted.safewallet.domain.auth.business.service;

import static com.wanted.safewallet.global.exception.ErrorCode.UNAUTHORIZED_JWT_TOKEN;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.wanted.safewallet.domain.auth.utils.JwtUtils;
import com.wanted.safewallet.domain.user.business.service.UserService;
import com.wanted.safewallet.global.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    AuthService authService;

    @Mock
    JwtUtils jwtTokenUtils;

    @Mock
    UserService userService;

    @Mock
    RefreshTokenService refreshTokenService;

    @DisplayName("JWT 토큰 재발급 서비스 테스트 : 실패 - 유효하지 않은 AT")
    @Test
    void reissueToken_invalid_at() {
        //given
        String accessToken = "testAccessToken";
        String refreshToken = "testRefreshToken";
        given(jwtTokenUtils.validateExpiredToken(accessToken)).willReturn(false);

        //when, then
        assertThatThrownBy(() -> authService.reissueToken(accessToken, refreshToken))
            .isInstanceOf(BusinessException.class)
            .extracting("errorCode").isEqualTo(UNAUTHORIZED_JWT_TOKEN);
    }

    @DisplayName("JWT 토큰 재발급 서비스 테스트 : 실패 - 만료된 RT")
    @Test
    void reissueToken_expired_rt() {
        //given
        String accessToken = "testAccessToken";
        String refreshToken = "testRefreshToken";
        given(jwtTokenUtils.validateExpiredToken(accessToken)).willReturn(true);
        given(jwtTokenUtils.validateToken(refreshToken)).willReturn(false);

        //when, then
        assertThatThrownBy(() -> authService.reissueToken(accessToken, refreshToken))
            .isInstanceOf(BusinessException.class)
            .extracting("errorCode").isEqualTo(UNAUTHORIZED_JWT_TOKEN);
    }

    @DisplayName("JWT 토큰 재발급 서비스 테스트 : 실패 - AT, RT 유저가 서로 다름")
    @Test
    void reissueToken_not_matched_at_rt() {
        //given
        String username = "testUsername";
        String accessToken = "testAccessToken";
        String refreshToken = "testRefreshToken";
        given(jwtTokenUtils.validateExpiredToken(accessToken)).willReturn(true);
        given(jwtTokenUtils.validateToken(refreshToken)).willReturn(true);
        given(jwtTokenUtils.getUsername(accessToken)).willReturn(username.toLowerCase());
        given(jwtTokenUtils.getUsername(refreshToken)).willReturn(username.toUpperCase());

        //when, then
        assertThatThrownBy(() -> authService.reissueToken(accessToken, refreshToken))
            .isInstanceOf(BusinessException.class)
            .extracting("errorCode").isEqualTo(UNAUTHORIZED_JWT_TOKEN);
    }

    @DisplayName("JWT 토큰 재발급 서비스 테스트 : 실패 - 존재하지 않는 RT")
    @Test
    void reissueToken_no_rt() {
        //given
        String username = "testUsername";
        String accessToken = "testAccessToken";
        String refreshToken = "testRefreshToken";
        given(jwtTokenUtils.validateExpiredToken(accessToken)).willReturn(true);
        given(jwtTokenUtils.validateToken(refreshToken)).willReturn(true);
        given(jwtTokenUtils.getUsername(accessToken)).willReturn(username);
        given(jwtTokenUtils.getUsername(refreshToken)).willReturn(username);
        given(refreshTokenService.validateToken(refreshToken)).willReturn(false);

        //when, then
        assertThatThrownBy(() -> authService.reissueToken(accessToken, refreshToken))
            .isInstanceOf(BusinessException.class)
            .extracting("errorCode").isEqualTo(UNAUTHORIZED_JWT_TOKEN);
    }
}
