package com.wanted.safewallet.domain.user.web.controller;

import static com.wanted.safewallet.global.exception.ErrorCode.ALREADY_AUTHENTICATED_MAIL;
import static com.wanted.safewallet.global.exception.ErrorCode.EXPIRED_MAIL_AUTH;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.wanted.safewallet.domain.user.business.facade.UserFacadeService;
import com.wanted.safewallet.global.dto.response.aop.PageStore;
import com.wanted.safewallet.global.exception.BusinessException;
import com.wanted.safewallet.utils.auth.WithMockCustomUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@Import(PageStore.class)
@WithMockCustomUser
@WebMvcTest(UserMailController.class)
class UserMailControllerTest {

    @MockBean
    UserFacadeService userFacadeService;

    @Autowired
    MockMvc mockMvc;

    @DisplayName("메일 인증 실패 시 예외 변환 테스트")
    @Test
    void authenticateMail_fail() throws Exception {
        //given
        willThrow(new BusinessException(EXPIRED_MAIL_AUTH))
            .given(userFacadeService).authenticateMail(anyString(), anyString());

        //when, then
        mockMvc.perform(get("/api/users/mail-auth")
                .param("email", "email@naver.com")
                .param("code", "mail-auth-code")
                .accept(MediaType.TEXT_HTML))
            .andExpect(status().isOk())
            .andExpect(view().name("mail-auth-fail"));
    }

    @DisplayName("인증 메일 재발송 실패 시 예외 변환 테스트")
    @Test
    void resendMailAuth_fail() throws Exception {
        //given
        willThrow(new BusinessException(ALREADY_AUTHENTICATED_MAIL))
            .given(userFacadeService).resendMailAuth(anyString());

        //when, then
        mockMvc.perform(post("/api/users/mail-auth")
                .param("email", "email@naver.com")
                .accept(MediaType.TEXT_HTML)
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(view().name("mail-auth-resend-fail"));
    }
}
