package com.wanted.safewallet.domain.auth.utils;

import static com.wanted.safewallet.global.exception.ErrorCode.EXPIRED_JWT_TOKEN;
import static com.wanted.safewallet.global.exception.ErrorCode.INVALID_JWT_TOKEN;

import com.wanted.safewallet.global.exception.BusinessException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtUtils {

    private static final String USER_ID_CLAIM_NAME = "userId";
    private static final String AUTHORITIES_CLAIM_NAME = "authorities";
    private final JwtProperties jwtProperties;

    public String generateAccessToken(String username, String userId, String authorities) {
        Instant issuedAt = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        Instant expiration = issuedAt.plus(jwtProperties.accessTokenExpirySec(), ChronoUnit.SECONDS);
        return Jwts.builder()
            .subject(username)
            .issuedAt(Date.from(issuedAt))
            .expiration(Date.from(expiration))
            .claim(USER_ID_CLAIM_NAME, userId)
            .claim(AUTHORITIES_CLAIM_NAME, authorities)
            .signWith(getKey())
            .compact();
    }

    public String generateRefreshToken(String username) {
        Instant issuedAt = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        Instant expiration = issuedAt.plus(jwtProperties.refreshTokenExpirySec(), ChronoUnit.SECONDS);
        return Jwts.builder()
            .subject(username)
            .issuedAt(Date.from(issuedAt))
            .expiration(Date.from(expiration))
            .signWith(getKey())
            .compact();
    }

    public String getUsername(String token) {
        return getClaims(token).getSubject();
    }

    public String getUserId(String token) {
        return getClaims(token).get(USER_ID_CLAIM_NAME, String.class);
    }

    public List<GrantedAuthority> getAuthorities(String token) {
        String authorities = getClaims(token).get(AUTHORITIES_CLAIM_NAME, String.class);
        return AuthorityUtils.commaSeparatedStringToAuthorityList(authorities);
    }

    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (BusinessException e) {
            return false;
        }
    }

    public boolean validateExpiredToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (BusinessException e) {
            return e.getErrorCode() == EXPIRED_JWT_TOKEN;
        }
    }

    private Claims getClaims(String token) {
        try {
            return Jwts.parser()
                .clockSkewSeconds(30)
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
            throw new BusinessException(EXPIRED_JWT_TOKEN);
        } catch (JwtException e) {
            log.error("JWT token is invalid: {}", e.getMessage());
            throw new BusinessException(INVALID_JWT_TOKEN);
        }
    }

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(jwtProperties.secretKey().getBytes(StandardCharsets.UTF_8));
    }
}
