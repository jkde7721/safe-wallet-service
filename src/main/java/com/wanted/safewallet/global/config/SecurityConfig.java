package com.wanted.safewallet.global.config;

import com.wanted.safewallet.domain.auth.filter.JwtAuthenticationFilter;
import com.wanted.safewallet.domain.auth.filter.LoginAuthenticationFilter;
import com.wanted.safewallet.domain.auth.handler.JwtAuthorizationFailureHandler;
import com.wanted.safewallet.domain.auth.handler.LoginAuthenticationFailureHandler;
import com.wanted.safewallet.domain.auth.handler.JwtAuthenticationFailureHandler;
import com.wanted.safewallet.domain.auth.handler.JwtLogoutSuccessHandler;
import com.wanted.safewallet.domain.auth.handler.LoginAuthenticationSuccessHandler;
import com.wanted.safewallet.domain.auth.utils.HeaderUtils;
import com.wanted.safewallet.domain.auth.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
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

    private final JwtUtils jwtutils;
    private final HeaderUtils headerUtils;
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
                .requestMatchers(HttpMethod.GET, "/docs/*.html").permitAll()
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

    @Bean
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        roleHierarchy.setHierarchy("ROLE_ADMIN > ROLE_USER > ROLE_ANONYMOUS");
        return roleHierarchy;
    }

    @Bean
    public MethodSecurityExpressionHandler methodSecurityExpressionHandler() {
        DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
        expressionHandler.setRoleHierarchy(roleHierarchy());
        return expressionHandler;
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
            JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtutils, headerUtils);

            http.addFilter(loginAuthenticationFilter)
                .addFilterAfter(jwtAuthenticationFilter, LoginAuthenticationFilter.class);
        }
    }
}
