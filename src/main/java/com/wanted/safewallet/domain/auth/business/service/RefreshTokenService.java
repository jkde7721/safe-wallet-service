package com.wanted.safewallet.domain.auth.business.service;

import com.wanted.safewallet.domain.auth.persistence.entity.RefreshToken;
import com.wanted.safewallet.domain.auth.persistence.repository.RefreshTokenRepository;
import com.wanted.safewallet.domain.auth.utils.JwtProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RefreshTokenService {

    private final JwtProperties jwtProperties;
    private final RefreshTokenRepository refreshTokenRepository;

    public void saveToken(String token) {
        RefreshToken refreshToken = RefreshToken.builder()
            .token(token).ttl(jwtProperties.refreshTokenExpirySec()).build();
        refreshTokenRepository.save(refreshToken);
    }

    public void deleteToken(String token) {
        refreshTokenRepository.findByToken(token)
            .ifPresent(refreshTokenRepository::delete);
    }

    public boolean validateToken(String token) {
        return refreshTokenRepository.findByToken(token).isPresent();
    }

    public void replaceToken(String originToken, String newToken) {
        deleteToken(originToken);
        saveToken(newToken);
    }
}
