package com.wanted.safewallet.domain.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wanted.safewallet.domain.auth.exception.IllegalLoginFormException;
import com.wanted.safewallet.domain.auth.web.dto.request.LoginRequestDto;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class LoginAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
        HttpServletResponse response) throws AuthenticationException {
        LoginRequestDto requestDto = getUsernameAndPassword(request);
        UsernamePasswordAuthenticationToken authRequest = UsernamePasswordAuthenticationToken
            .unauthenticated(requestDto.getUsername(), requestDto.getPassword());
        return getAuthenticationManager().authenticate(authRequest);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
        FilterChain chain, Authentication authentication) throws ServletException, IOException {
        getSuccessHandler().onAuthenticationSuccess(request, response, authentication);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
        AuthenticationException authException) throws IOException, ServletException {
        getFailureHandler().onAuthenticationFailure(request, response, authException);
    }

    private LoginRequestDto getUsernameAndPassword(HttpServletRequest request) {
        try {
            return objectMapper.readValue(request.getInputStream(), LoginRequestDto.class);
        } catch (IOException e) {
            throw new IllegalLoginFormException("잘못된 로그인 요청 형식입니다.");
        }
    }
}
