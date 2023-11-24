package com.wanted.safewallet.domain.auth.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.wanted.safewallet.config.JwtPropertiesConfiguration;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@JwtPropertiesConfiguration
class JwtPropertiesTest {

    static final int MIN_BYTES_SIZE = 32;
    static final long MIN_AT_EXPIRY_SEC = 30 * 60;
    static final long MIN_RT_EXPIRY_SEC = 5 * 24 * 60 * 60;

    @Autowired
    JwtProperties jwtProperties;

    @DisplayName("JWT 프로퍼티 yml 파일 설정 확인")
    @Test
    void jwtPropertiesCheck() {
        assertAll(
            () -> assertThat(jwtProperties.prefix()).isEqualTo("Bearer "),
            () -> assertThat(jwtProperties.secretKey().getBytes(StandardCharsets.UTF_8))
                .hasSizeGreaterThanOrEqualTo(MIN_BYTES_SIZE),
            () -> assertThat(jwtProperties.accessTokenExpirySec()).isGreaterThanOrEqualTo(MIN_AT_EXPIRY_SEC),
            () -> assertThat(jwtProperties.refreshTokenExpirySec()).isGreaterThanOrEqualTo(MIN_RT_EXPIRY_SEC)
        );
    }
}
