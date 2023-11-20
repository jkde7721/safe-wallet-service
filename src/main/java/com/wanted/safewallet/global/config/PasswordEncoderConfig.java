package com.wanted.safewallet.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class PasswordEncoderConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        //기본적으로 BCryptPasswordEncoder 사용
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
