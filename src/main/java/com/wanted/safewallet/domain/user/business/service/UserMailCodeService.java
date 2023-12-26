package com.wanted.safewallet.domain.user.business.service;

import static com.wanted.safewallet.global.exception.ErrorCode.EXPIRED_MAIL_AUTH;

import com.wanted.safewallet.global.exception.BusinessException;
import java.time.Duration;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class UserMailCodeService {

    private final StringRedisTemplate redisTemplate;
    private final long mailAuthExpirySec;

    public UserMailCodeService(StringRedisTemplate redisTemplate,
        @Value("${mail-auth-expiry-sec}") long mailAuthExpirySec) {
        this.redisTemplate = redisTemplate;
        this.mailAuthExpirySec = mailAuthExpirySec;
    }

    public void saveMailCode(String email, String code) {
        redisTemplate.opsForValue()
            .set(email, code, Duration.ofSeconds(mailAuthExpirySec));
    }

    public void validateMailCode(String email, String code) {
        String savedCode = redisTemplate.opsForValue().getAndDelete(email);
        if (!Objects.equals(savedCode, code)) {
            throw new BusinessException(EXPIRED_MAIL_AUTH);
        }
    }
}
