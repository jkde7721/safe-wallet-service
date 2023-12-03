package com.wanted.safewallet.domain.auth.config;

import com.wanted.safewallet.config.JwtPropertiesConfiguration;
import com.wanted.safewallet.domain.auth.business.service.AuthService;
import com.wanted.safewallet.domain.auth.business.service.CustomUserDetailsService;
import com.wanted.safewallet.domain.auth.business.service.RefreshTokenService;
import com.wanted.safewallet.domain.auth.handler.JwtAuthenticationFailureHandler;
import com.wanted.safewallet.domain.auth.handler.JwtAuthorizationFailureHandler;
import com.wanted.safewallet.domain.auth.handler.JwtLogoutSuccessHandler;
import com.wanted.safewallet.domain.auth.handler.LoginAuthenticationFailureHandler;
import com.wanted.safewallet.domain.auth.handler.LoginAuthenticationSuccessHandler;
import com.wanted.safewallet.domain.auth.utils.CookieUtils;
import com.wanted.safewallet.domain.auth.utils.HeaderUtils;
import com.wanted.safewallet.domain.auth.utils.JwtUtils;
import com.wanted.safewallet.global.config.PasswordEncoderConfig;
import com.wanted.safewallet.global.config.SecurityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

@Import({JwtUtils.class, HeaderUtils.class, CookieUtils.class,
    LoginAuthenticationSuccessHandler.class, LoginAuthenticationFailureHandler.class,
    JwtAuthenticationFailureHandler.class, JwtAuthorizationFailureHandler.class,
    JwtLogoutSuccessHandler.class, PasswordEncoderConfig.class, SecurityConfig.class})
@MockBean(classes = {AuthService.class, CustomUserDetailsService.class, RefreshTokenService.class})
@JwtPropertiesConfiguration
@TestConfiguration
public class AuthTestConfig {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    UserDetailsService userDetailsService;

    //Spring Security Filter 내 UsernamePasswordAuthenticationToken 처리하는 Provider 빈 등록 (통합 테스트 시에는 스프링이 자동 등록)
    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider(passwordEncoder);
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        return daoAuthenticationProvider;
    }
}
