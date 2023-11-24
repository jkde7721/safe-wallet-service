package com.wanted.safewallet.domain.auth.web.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.wanted.safewallet.config.JwtPropertiesConfiguration;
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
import org.springframework.test.web.servlet.MockMvc;

@Import({HeaderUtils.class, CookieUtils.class})
@JwtPropertiesConfiguration
@WithMockCustomUser
@WebMvcTest(AuthController.class)
class AuthControllerTest {

    static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";
    static final String AUTHORIZATION_HEADER_NAME = "Authorization";

    @MockBean
    AuthService authService;

    @Autowired
    JwtProperties jwtProperties;

    @Autowired
    MockMvc mockMvc;

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
        mockMvc.perform(put("/api/auth/refresh")
                .header(AUTHORIZATION_HEADER_NAME, jwtProperties.prefix() + accessToken)
                .cookie(new Cookie(REFRESH_TOKEN_COOKIE_NAME, refreshToken))
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(header().stringValues(AUTHORIZATION_HEADER_NAME, jwtProperties.prefix() + newAccessToken))
            .andExpect(cookie().value(REFRESH_TOKEN_COOKIE_NAME, newRefreshToken))
            .andDo(print());
    }
}
