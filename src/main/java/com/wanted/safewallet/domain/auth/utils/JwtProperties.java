package com.wanted.safewallet.domain.auth.utils;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(String prefix,
                            String secretKey,
                            long accessTokenExpirySec,
                            long refreshTokenExpirySec) {

}
