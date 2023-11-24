package com.wanted.safewallet.domain.auth.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.lenient;
import static org.springframework.util.StringUtils.collectionToCommaDelimitedString;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;

@ExtendWith(MockitoExtension.class)
class JwtUtilsTest {

    static final String USERNAME = "testUsername";
    static final String USER_ID = "testUserId";
    static final String AUTHORITIES = "ROLE_USER";
    static final String SECRET_KEY = "vinJ4piZLXCEu7TMXkKl1TGJQlpjKsdj";
    static final long AT_EXPIRY_SEC = 30 * 60;
    static final long RT_EXPIRY_SEC = 5 * 24 * 60 * 60;

    @InjectMocks
    JwtUtils jwtUtils;

    @Mock
    JwtProperties jwtProperties;

    @BeforeEach
    void init() {
        lenient().when(jwtProperties.accessTokenExpirySec()).thenReturn(AT_EXPIRY_SEC);
        lenient().when(jwtProperties.refreshTokenExpirySec()).thenReturn(RT_EXPIRY_SEC);
        lenient().when(jwtProperties.secretKey()).thenReturn(SECRET_KEY);
    }

    @DisplayName("Access Token 발급 테스트 : 성공")
    @Test
    void generateAccessToken() {
        //when
        String accessToken = jwtUtils.generateAccessToken(USERNAME, USER_ID, AUTHORITIES);

        //then
        assertThat(accessToken).isNotBlank();
    }

    @DisplayName("Refresh Token 발급 테스트 : 성공")
    @Test
    void generateRefreshToken() {
        //when
        String refreshToken = jwtUtils.generateRefreshToken(USERNAME);

        //then
        assertThat(refreshToken).isNotBlank();
    }

    @DisplayName("JWT 토큰으로부터 username 조회 테스트 : 성공")
    @Test
    void getUsername() {
        //given
        String token = jwtUtils.generateAccessToken(USERNAME, USER_ID, AUTHORITIES);

        //when
        String username = jwtUtils.getUsername(token);

        //then
        assertThat(username).isEqualTo(USERNAME);
    }

    @DisplayName("JWT 토큰으로부터 userId 조회 테스트 : 성공")
    @Test
    void getUserId() {
        //given
        String token = jwtUtils.generateAccessToken(USERNAME, USER_ID, AUTHORITIES);

        //when
        String userId = jwtUtils.getUserId(token);

        //then
        assertThat(userId).isEqualTo(USER_ID);
    }

    @DisplayName("JWT 토큰으로부터 authorities 조회 테스트 : 성공")
    @Test
    void getAuthorities() {
        //given
        String token = jwtUtils.generateAccessToken(USERNAME, USER_ID, AUTHORITIES);

        //when
        List<GrantedAuthority> authorities = jwtUtils.getAuthorities(token);

        //then
        assertThat(collectionToCommaDelimitedString(authorities)).isEqualTo(AUTHORITIES);
    }

    @DisplayName("JWT 토큰 검증 테스트 : 성공")
    @Test
    void validateToken() {
        //given
        String token = jwtUtils.generateAccessToken(USERNAME, USER_ID, AUTHORITIES);

        //when
        boolean isValidated = jwtUtils.validateToken(token);

        //then
        assertThat(isValidated).isTrue();
    }

    @DisplayName("JWT 토큰 검증 테스트 : 실패 - 만료된 토큰")
    @Test
    void validateToken_expired_token() {
        //given
        given(jwtProperties.accessTokenExpirySec()).willReturn(-60L);
        String token = jwtUtils.generateAccessToken(USERNAME, USER_ID, AUTHORITIES);

        //when
        boolean isValidated = jwtUtils.validateToken(token);

        //then
        assertThat(isValidated).isFalse();
    }

    @DisplayName("JWT 토큰 검증 테스트 : 실패 - 조작된 토큰")
    @Test
    void validateToken_invalid_token() {
        //given
        String token = "invalidToken";

        //when
        boolean isValidated = jwtUtils.validateToken(token);

        //then
        assertThat(isValidated).isFalse();
    }

    @DisplayName("만료된 JWT 토큰 검증 테스트 : 성공")
    @Test
    void validateExpiredToken() {
        //given
        given(jwtProperties.accessTokenExpirySec()).willReturn(-60L);
        String token = jwtUtils.generateAccessToken(USERNAME, USER_ID, AUTHORITIES);

        //when
        boolean isValidated = jwtUtils.validateExpiredToken(token);

        //then
        assertThat(isValidated).isTrue();
    }

    @DisplayName("만료된 JWT 토큰 검증 테스트 : 실패 - 조작된 토큰")
    @Test
    void validateExpiredToken_invalid_token() {
        //given
        String token = "invalidToken";

        //when
        boolean isValidated = jwtUtils.validateExpiredToken(token);

        //then
        assertThat(isValidated).isFalse();
    }
}
