package com.wanted.safewallet.domain.auth.business.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.wanted.safewallet.config.JwtPropertiesConfiguration;
import com.wanted.safewallet.domain.auth.persistence.entity.RefreshToken;
import com.wanted.safewallet.domain.auth.persistence.repository.RefreshTokenRepository;
import com.wanted.safewallet.domain.auth.utils.JwtProperties;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

@JwtPropertiesConfiguration
@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    RefreshTokenService refreshTokenService;

    @Autowired
    JwtProperties jwtProperties;

    @Mock
    RefreshTokenRepository refreshTokenRepository;

    @Captor
    ArgumentCaptor<RefreshToken> refreshTokenCaptor;

    @BeforeEach
    void init() {
        refreshTokenService = new RefreshTokenService(jwtProperties, refreshTokenRepository);
    }

    @DisplayName("Refresh Token 저장 테스트 : 성공")
    @Test
    void saveToken() {
        //given
        String token = "testToken";

        //when
        refreshTokenService.saveToken(token);

        //then
        then(refreshTokenRepository).should(times(1)).save(refreshTokenCaptor.capture());
        assertThat(refreshTokenCaptor.getValue().getToken()).isEqualTo(token);
        assertThat(refreshTokenCaptor.getValue().getTtl()).isEqualTo(jwtProperties.refreshTokenExpirySec());
    }

    @DisplayName("Refresh Token 삭제 테스트 : 성공")
    @Test
    void deleteToken() {
        //given
        String token = "testToken";
        RefreshToken refreshToken = RefreshToken.builder().token(token).build();
        given(refreshTokenRepository.findByToken(anyString())).willReturn(Optional.of(refreshToken));

        //when
        refreshTokenService.deleteToken(token);

        //then
        then(refreshTokenRepository).should(times(1)).delete(refreshToken);
    }

    @DisplayName("Refresh Token 검증 테스트 : 성공")
    @Test
    void validateToken() {
        //given
        String token = "testToken";
        RefreshToken refreshToken = RefreshToken.builder().token(token).build();
        given(refreshTokenRepository.findByToken(anyString())).willReturn(Optional.of(refreshToken));

        //when
        boolean isValidated = refreshTokenService.validateToken(token);

        //then
        assertThat(isValidated).isTrue();
    }

    @DisplayName("Refresh Token 검증 테스트 : 실패 - 해당 토큰 없음")
    @Test
    void validateToken_no_token() {
        //given
        String token = "testToken";
        given(refreshTokenRepository.findByToken(anyString())).willReturn(Optional.empty());

        //when
        boolean isValidated = refreshTokenService.validateToken(token);

        //then
        assertThat(isValidated).isFalse();
    }

    @DisplayName("Refresh Token 변경 테스트 : 성공")
    @Test
    void replaceToken() {
        //given
        String originToken = "testOriginToken";
        String newToken = "testNewToken";
        RefreshToken originRefreshToken = RefreshToken.builder().token(originToken).build();
        given(refreshTokenRepository.findByToken(anyString())).willReturn(Optional.of(originRefreshToken));

        //when
        refreshTokenService.replaceToken(originToken, newToken);

        //then
        then(refreshTokenRepository).should(times(1)).delete(originRefreshToken);
        then(refreshTokenRepository).should(times(1)).save(refreshTokenCaptor.capture());
        assertThat(refreshTokenCaptor.getValue().getToken()).isEqualTo(newToken);
    }
}
