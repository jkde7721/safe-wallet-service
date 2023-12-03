package com.wanted.safewallet.domain.auth.web.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.cookies.CookieDocumentation.responseCookies;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.wanted.safewallet.config.JwtPropertiesConfiguration;
import com.wanted.safewallet.docs.common.AbstractRestDocsTest;
import com.wanted.safewallet.domain.auth.business.dto.response.JwtResponseDto;
import com.wanted.safewallet.domain.auth.business.service.AuthService;
import com.wanted.safewallet.domain.auth.utils.CookieUtils;
import com.wanted.safewallet.domain.auth.utils.HeaderUtils;
import com.wanted.safewallet.domain.auth.utils.JwtProperties;
import com.wanted.safewallet.utils.auth.WithMockCustomUser;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;

@Import({HeaderUtils.class, CookieUtils.class})
@JwtPropertiesConfiguration
@WithMockCustomUser
@WebMvcTest(AuthController.class)
class AuthControllerTest extends AbstractRestDocsTest {

    static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";
    static final String AUTHORIZATION_HEADER_NAME = "Authorization";

    @MockBean
    AuthService authService;

    @Autowired
    JwtProperties jwtProperties;

    @DisplayName("RT를 통한 토큰 재발급 테스트 : 성공")
    @Test
    void refresh() throws Exception {
        //given
        String accessToken = "testAccessToken";
        String refreshToken = "testRefreshToken";
        String newAccessToken = "newAccessToken";
        String newRefreshToken = "newRefreshToken";
        given(authService.reissueToken(accessToken, refreshToken))
            .willReturn(new JwtResponseDto(newAccessToken, newRefreshToken));

        //when, then
        restDocsMockMvc.perform(put("/api/auth/refresh")
                .header(AUTHORIZATION_HEADER_NAME, jwtProperties.prefix() + accessToken)
                .cookie(new Cookie(REFRESH_TOKEN_COOKIE_NAME, refreshToken))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(header().string(AUTHORIZATION_HEADER_NAME, jwtProperties.prefix() + newAccessToken))
            .andExpect(cookie().value(REFRESH_TOKEN_COOKIE_NAME, newRefreshToken))
            .andDo(restDocs.document(
                requestHeaders(
                    headerWithName("Authorization").description("만료된 Access Token 값")
                        .attributes(key("formats").value("'Bearer ' prefix 필수"))),
                requestCookies(
                    cookieWithName("refreshToken").description("유효한 Refresh Token 값")),
                responseHeaders(
                    headerWithName("Authorization").description("새로 발급된 Access Token 값")
                        .attributes(key("formats").value("기존 토큰에 'Bearer ' prefix 추가된 형태"))),
                responseCookies(
                    cookieWithName("refreshToken").description("새로 발급된 Refresh Token 값"))
            ));
    }
}
