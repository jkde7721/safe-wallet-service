package com.wanted.safewallet.domain.auth.utils;

import static org.assertj.core.api.Assertions.assertThat;

import com.wanted.safewallet.config.JwtPropertiesConfiguration;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

@Import(CookieUtils.class)
@JwtPropertiesConfiguration
class CookieUtilsTest {

    static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";
    static final String REFRESH_TOKEN_COOKIE_PATH = "/api/auth";

    @Autowired
    CookieUtils cookieUtils;

    @Autowired
    JwtProperties jwtProperties;

    @DisplayName("요청 쿠키에서 토큰 조회 테스트 : 성공")
    @Test
    void getToken() {
        //given
        String token = "testToken";
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie(REFRESH_TOKEN_COOKIE_NAME, token));

        //when
        String foundToken = cookieUtils.getToken(request);

        //then
        assertThat(foundToken).isEqualTo(token);
    }

    @DisplayName("요청 쿠키에서 토큰 조회 테스트 : 실패")
    @Test
    void getToken_no_cookie() {
        //given
        MockHttpServletRequest request = new MockHttpServletRequest();

        //when
        String token = cookieUtils.getToken(request);

        //then
        assertThat(token).isNull();
    }

    @DisplayName("응답 쿠키에 토큰 저장 테스트 : 성공")
    @Test
    void setToken() {
        //given
        String token = "testToken";
        MockHttpServletResponse response = new MockHttpServletResponse();

        //when
        cookieUtils.setToken(response, token);

        //then
        Cookie cookie = response.getCookie(REFRESH_TOKEN_COOKIE_NAME);
        assertThat(cookie).isNotNull();
        assertThat(cookie.getValue()).isEqualTo(token);
        assertThat(cookie.isHttpOnly()).isTrue();
        assertThat(cookie.getMaxAge()).isEqualTo(jwtProperties.refreshTokenExpirySec());
        assertThat(cookie.getPath()).isEqualTo(REFRESH_TOKEN_COOKIE_PATH);
        assertThat(cookie.getSecure()).isFalse();
    }

    @DisplayName("토큰 쿠키 삭제 테스트 : 성공")
    @Test
    void deleteToken() {
        //given
        String token = "testToken";
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setCookies(new Cookie(REFRESH_TOKEN_COOKIE_NAME, token));

        //when
        cookieUtils.deleteToken(request, response);

        //then
        Cookie cookie = response.getCookie(REFRESH_TOKEN_COOKIE_NAME);
        assertThat(cookie).isNotNull();
        assertThat(cookie.getMaxAge()).isZero();
    }
}
