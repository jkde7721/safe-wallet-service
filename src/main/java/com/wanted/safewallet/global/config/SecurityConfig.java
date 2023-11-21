package com.wanted.safewallet.global.config;

import com.wanted.safewallet.domain.auth.filter.JwtAuthenticationFilter;
import com.wanted.safewallet.domain.auth.filter.LoginAuthenticationFilter;
import com.wanted.safewallet.domain.auth.handler.JwtAuthorizationFailureHandler;
import com.wanted.safewallet.domain.auth.handler.LoginAuthenticationFailureHandler;
import com.wanted.safewallet.domain.auth.handler.JwtAuthenticationFailureHandler;
import com.wanted.safewallet.domain.auth.handler.JwtLogoutSuccessHandler;
import com.wanted.safewallet.domain.auth.handler.LoginAuthenticationSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final LoginAuthenticationSuccessHandler loginAuthenticationSuccessHandler;
    private final LoginAuthenticationFailureHandler loginAuthenticationFailureHandler;
    private final JwtAuthenticationFailureHandler jwtAuthenticationFailureHandler;
    private final JwtAuthorizationFailureHandler jwtAuthorizationFailureHandler;
    private final JwtLogoutSuccessHandler jwtLogoutSuccessHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.apply(new CustomFilterConfigurer());
        http
            .authorizeHttpRequests(request -> request
                .requestMatchers("/api/auth/**", "/api/users/**").permitAll()
                .anyRequest().authenticated()
            )
            .sessionManagement(
                config -> config.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .csrf(AbstractHttpConfigurer::disable)
            .exceptionHandling(config -> {
                config.authenticationEntryPoint(jwtAuthenticationFailureHandler);
                config.accessDeniedHandler(jwtAuthorizationFailureHandler);
            })
            .logout(config -> config.logoutSuccessHandler(jwtLogoutSuccessHandler)
                .logoutUrl("/api/auth/logout"));
        return http.build();
    }

    public class CustomFilterConfigurer extends
        AbstractHttpConfigurer<CustomFilterConfigurer, HttpSecurity> {

        @Override
        public void configure(HttpSecurity http) {
            AuthenticationManager authenticationManager = http.getSharedObject(
                AuthenticationManager.class);
            LoginAuthenticationFilter loginAuthenticationFilter = new LoginAuthenticationFilter();
            loginAuthenticationFilter.setFilterProcessesUrl("/api/auth/login");
            loginAuthenticationFilter.setAuthenticationManager(authenticationManager);
            loginAuthenticationFilter.setAuthenticationSuccessHandler(loginAuthenticationSuccessHandler);
            loginAuthenticationFilter.setAuthenticationFailureHandler(loginAuthenticationFailureHandler);

            http.addFilter(loginAuthenticationFilter)
                .addFilterAfter(jwtAuthenticationFilter, LoginAuthenticationFilter.class);
        }
    }
}
