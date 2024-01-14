package com.wanted.safewallet.domain.auth.web.controller;

import static com.wanted.safewallet.utils.Fixtures.anUser;
import static com.wanted.safewallet.utils.JsonUtils.asJsonString;
import static org.hamcrest.Matchers.startsWith;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.cookies.CookieDocumentation.responseCookies;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.wanted.safewallet.docs.common.AbstractRestDocsTest;
import com.wanted.safewallet.domain.auth.business.dto.CustomUserDetails;
import com.wanted.safewallet.domain.auth.business.dto.JwtDto;
import com.wanted.safewallet.domain.auth.business.facade.AuthFacadeService;
import com.wanted.safewallet.domain.auth.business.service.CustomUserDetailsService;
import com.wanted.safewallet.domain.auth.business.service.RefreshTokenService;
import com.wanted.safewallet.domain.auth.config.AuthTestConfig;
import com.wanted.safewallet.domain.auth.utils.JwtProperties;
import com.wanted.safewallet.domain.auth.web.dto.request.LoginRequest;
import com.wanted.safewallet.domain.user.persistence.entity.User;
import com.wanted.safewallet.global.dto.response.aop.PageStore;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;

@Import({AuthTestConfig.class, PageStore.class})
@WebMvcTest(AuthController.class)
class AuthControllerTest extends AbstractRestDocsTest {

    static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";
    static final String AUTHORIZATION_HEADER_NAME = "Authorization";

    @Autowired
    AuthFacadeService authFacadeService;

    @Autowired
    CustomUserDetailsService customUserDetailsService;

    @Autowired
    RefreshTokenService refreshTokenService;

    @Autowired
    JwtProperties jwtProperties;

    @Autowired
    PasswordEncoder passwordEncoder;

    @DisplayName("사용자 로그인 테스트 : 성공")
    @Test
    void login() throws Exception {
        //given
        String password = "password";
        String encodedPassword = passwordEncoder.encode(password);
        User user = anUser().password(encodedPassword).build();
        CustomUserDetails customUserDetails = new CustomUserDetails(user);
        given(customUserDetailsService.loadUserByUsername(anyString())).willReturn(customUserDetails);

        //when, then
        LoginRequest request = new LoginRequest(user.getUsername(), password);
        authRestDocsMockMvc.perform(post("/api/auth/login")
                .content(asJsonString(request))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(header().string(AUTHORIZATION_HEADER_NAME, startsWith(jwtProperties.prefix())))
            .andExpect(cookie().exists(REFRESH_TOKEN_COOKIE_NAME))
            .andDo(restDocs.document(
                requestFields(
                    fieldWithPath("username").description("계정명")
                        .attributes(key("formats").value("이메일 형식"))
                        .attributes(key("constraints").value("회원가입한 유저의 계정명")),
                    fieldWithPath("password").description("비밀번호")
                        .attributes(key("constraints").value("회원가입한 유저의 비밀번호"))),
                responseHeaders(
                    headerWithName(AUTHORIZATION_HEADER_NAME).description("새로 발급된 Access Token 값")
                        .attributes(key("formats").value("기존 토큰에 'Bearer ' prefix 추가된 형태"))
                        .attributes(key("expirySec").value(jwtProperties.accessTokenExpirySec()))),
                responseCookies(
                    cookieWithName(REFRESH_TOKEN_COOKIE_NAME).description("새로 발급된 Refresh Token 값")
                        .attributes(key("maxAge").value(jwtProperties.refreshTokenExpirySec())))
            ));
        then(refreshTokenService).should(times(1)).saveToken(anyString());
    }

    @DisplayName("사용자 로그아웃 테스트 : 성공")
    @Test
    void logout() throws Exception {
        //given
        String refreshToken = "testRefreshToken";

        //when, then
        authRestDocsMockMvc.perform(put("/api/auth/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(new Cookie(REFRESH_TOKEN_COOKIE_NAME, refreshToken)))
            .andExpect(status().isOk())
            .andExpect(cookie().maxAge(REFRESH_TOKEN_COOKIE_NAME, 0))
            .andDo(restDocs.document(
                requestCookies(
                    cookieWithName(REFRESH_TOKEN_COOKIE_NAME).description("Refresh Token 값")),
                responseCookies(
                    cookieWithName(REFRESH_TOKEN_COOKIE_NAME).description("삭제된 Refresh Token")
                        .attributes(key("maxAge").value(0)))
            ));
    }

    @DisplayName("RT를 통한 토큰 재발급 테스트 : 성공")
    @Test
    void refresh() throws Exception {
        //given
        String accessToken = "testAccessToken";
        String refreshToken = "testRefreshToken";
        String newAccessToken = "newAccessToken";
        String newRefreshToken = "newRefreshToken";
        given(authFacadeService.reissueToken(accessToken, refreshToken))
            .willReturn(new JwtDto(newAccessToken, newRefreshToken));

        //when, then
        authRestDocsMockMvc.perform(put("/api/auth/refresh")
                .header(AUTHORIZATION_HEADER_NAME, jwtProperties.prefix() + accessToken)
                .cookie(new Cookie(REFRESH_TOKEN_COOKIE_NAME, refreshToken))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(header().string(AUTHORIZATION_HEADER_NAME, jwtProperties.prefix() + newAccessToken))
            .andExpect(cookie().value(REFRESH_TOKEN_COOKIE_NAME, newRefreshToken))
            .andDo(restDocs.document(
                requestHeaders(
                    headerWithName(AUTHORIZATION_HEADER_NAME).description("만료된 Access Token 값")
                        .attributes(key("formats").value("'Bearer ' prefix 필수"))),
                requestCookies(
                    cookieWithName(REFRESH_TOKEN_COOKIE_NAME).description("유효한 Refresh Token 값")),
                responseHeaders(
                    headerWithName(AUTHORIZATION_HEADER_NAME).description("새로 발급된 Access Token 값")
                        .attributes(key("formats").value("기존 토큰에 'Bearer ' prefix 추가된 형태"))
                        .attributes(key("expirySec").value(jwtProperties.accessTokenExpirySec()))),
                responseCookies(
                    cookieWithName(REFRESH_TOKEN_COOKIE_NAME).description("새로 발급된 Refresh Token 값")
                        .attributes(key("maxAge").value(jwtProperties.refreshTokenExpirySec())))
            ));
    }
}
