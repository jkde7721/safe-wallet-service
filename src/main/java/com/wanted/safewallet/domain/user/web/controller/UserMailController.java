package com.wanted.safewallet.domain.user.web.controller;

import com.wanted.safewallet.domain.user.business.facade.UserFacadeService;
import com.wanted.safewallet.domain.user.web.dto.request.UserMailRequest;
import com.wanted.safewallet.global.exception.BusinessException;
import com.wanted.safewallet.global.exception.BusinessTemplateException;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
@RequestMapping("/api/users/mail-auth")
@Controller
public class UserMailController {

    private final UserFacadeService userFacadeService;

    /*
    실패 상황
    - 메일 코드 불일치
    - 유저 존재하지 않음
    - 유저가 이미 USER 권한 획득
     */
    @GetMapping
    public String authenticateMail(@RequestParam String email, @RequestParam String code) {
        try {
            userFacadeService.authenticateMail(email, code);
        } catch (BusinessException e) {
            throw new BusinessTemplateException(
                e, Map.of("userMailRequest", new UserMailRequest(email)), "mail-auth-fail");
        }
        return "mail-auth-success";
    }

    /*
    실패 상황
    - 유저 존재하지 않음
    - 유저가 이미 USER 권한 획득
    - 메일 전송에 실패
    - TODO: 기존 메일 코드 만료 시간이 5분 이상 남음
     */
    @PostMapping
    public String resendMailAuth(@ModelAttribute @Valid UserMailRequest request, BindingResult bindingResult) {
        if (bindingResult.hasFieldErrors()) {
            return "mail-auth-fail";
        }
        try {
            userFacadeService.resendMailAuth(request.getEmail());
        } catch (BusinessException e) {
            throw new BusinessTemplateException(e, "mail-auth-resend-fail");
        }
        return "mail-auth-resend-success";
    }
}
