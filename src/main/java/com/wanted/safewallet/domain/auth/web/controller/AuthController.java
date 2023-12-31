package com.wanted.safewallet.domain.auth.web.controller;

import com.wanted.safewallet.domain.auth.business.dto.JwtDto;
import com.wanted.safewallet.domain.auth.business.facade.AuthFacadeService;
import com.wanted.safewallet.domain.auth.utils.CookieUtils;
import com.wanted.safewallet.domain.auth.utils.HeaderUtils;
import com.wanted.safewallet.global.dto.response.aop.CommonResponseContent;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CommonResponseContent
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@RestController
public class AuthController {

    private final AuthFacadeService authFacadeService;
    private final HeaderUtils headerUtils;
    private final CookieUtils cookieUtils;

    @PutMapping("/refresh")
    public void refresh(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = headerUtils.getToken(request);
        String refreshToken = cookieUtils.getToken(request);
        JwtDto jwtDto = authFacadeService.reissueToken(accessToken, refreshToken);
        storeTokenToResponse(response, jwtDto);
    }

    private void storeTokenToResponse(HttpServletResponse response, JwtDto jwtDto) {
        headerUtils.setToken(response, jwtDto.getAccessToken());
        cookieUtils.setToken(response, jwtDto.getRefreshToken());
    }
}
