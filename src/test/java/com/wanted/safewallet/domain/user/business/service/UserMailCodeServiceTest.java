package com.wanted.safewallet.domain.user.business.service;

import static com.wanted.safewallet.global.exception.ErrorCode.EXPIRED_MAIL_AUTH;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

import com.wanted.safewallet.global.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

@ExtendWith(MockitoExtension.class)
class UserMailCodeServiceTest {

    UserMailCodeService userMailCodeService;

    @Mock
    StringRedisTemplate redisTemplate;

    @BeforeEach
    void init() {
        userMailCodeService = new UserMailCodeService(redisTemplate, 600);
    }

    @DisplayName("인증용 메일 코드 검증 서비스 테스트 : 실패 - 만료된 메일 인증")
    @Test
    void validateMailCode() {
        //given
        String email = "testUsername@naver.com";
        String code = "testCode";
        String savedCode = "anotherCode";
        ValueOperations<String, String> valueOperations = mock(ValueOperations.class);
        given(redisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.getAndDelete(anyString())).willReturn(savedCode);

        //when, then
        assertThatThrownBy(() -> userMailCodeService.validateMailCode(email, code))
            .isInstanceOf(BusinessException.class)
            .extracting("errorCode").isEqualTo(EXPIRED_MAIL_AUTH);
        then(redisTemplate).should(times(1)).opsForValue();
        then(valueOperations).should(times(1)).getAndDelete(email);
    }
}
