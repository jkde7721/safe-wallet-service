package com.wanted.safewallet.domain.auth.utils;

import static org.assertj.core.api.Assertions.assertThat;

import com.wanted.safewallet.config.JwtPropertiesConfiguration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

@Import(HeaderUtils.class)
@JwtPropertiesConfiguration
class HeaderUtilsTest {

    static final String AUTHORIZATION_HEADER_NAME = "Authorization";

    @Autowired
    HeaderUtils headerUtils;

    @Autowired
    JwtProperties jwtProperties;

    @DisplayName("요청 헤더에서 토큰 조회 테스트 : 성공")
    @Test
    void getToken() {
        //given
        String token = "testToken";
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(AUTHORIZATION_HEADER_NAME, jwtProperties.prefix() + token);

        //when
        String foundToken = headerUtils.getToken(request);

        //then
        assertThat(foundToken).isEqualTo(token);
    }

    @DisplayName("요청 헤더에서 토큰 조회 테스트 : 실패")
    @Test
    void getToken_no_header() {
        //given
        MockHttpServletRequest request = new MockHttpServletRequest();

        //when
        String token = headerUtils.getToken(request);

        //then
        assertThat(token).isNull();
    }

    @DisplayName("응답 헤더에 토큰 저장 테스트 : 성공")
    @Test
    void setToken() {
        //given
        String token = "testToken";
        MockHttpServletResponse response = new MockHttpServletResponse();

        //when
        headerUtils.setToken(response, token);

        //then
        String foundToken = response.getHeader(AUTHORIZATION_HEADER_NAME);
        assertThat(foundToken).isNotNull().startsWith(jwtProperties.prefix());
        assertThat(foundToken.substring(jwtProperties.prefix().length())).isEqualTo(token);
    }
}
