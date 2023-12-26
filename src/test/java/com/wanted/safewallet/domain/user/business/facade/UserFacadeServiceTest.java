package com.wanted.safewallet.domain.user.business.facade;

import static com.wanted.safewallet.global.exception.ErrorCode.ALREADY_AUTHENTICATED_MAIL;
import static com.wanted.safewallet.global.exception.ErrorCode.EXPIRED_MAIL_AUTH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.times;

import com.wanted.safewallet.domain.user.business.mapper.UserMapper;
import com.wanted.safewallet.domain.user.business.service.UserMailCodeService;
import com.wanted.safewallet.domain.user.business.service.UserService;
import com.wanted.safewallet.domain.user.persistence.entity.User;
import com.wanted.safewallet.domain.user.web.dto.request.UserJoinRequest;
import com.wanted.safewallet.domain.user.web.dto.response.UsernameCheckResponse;
import com.wanted.safewallet.global.config.PasswordEncoderConfig;
import com.wanted.safewallet.global.exception.BusinessException;
import com.wanted.safewallet.global.exception.BusinessTemplateException;
import com.wanted.safewallet.global.mail.dto.MailMessage;
import com.wanted.safewallet.global.mail.service.MailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@Import(PasswordEncoderConfig.class)
@ExtendWith({MockitoExtension.class, SpringExtension.class})
class UserFacadeServiceTest {

    UserFacadeService userFacadeService;

    @Spy
    UserMapper userMapper;

    @Mock
    UserService userService;

    @Mock
    UserMailCodeService userMailCodeService;

    @Mock
    MailService mailService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Captor
    ArgumentCaptor<User> userCaptor;

    @BeforeEach
    void init() {
        userFacadeService = new UserFacadeService(userMapper, userService, userMailCodeService, mailService, passwordEncoder);
    }

    @DisplayName("유저 계정명 중복 여부 확인 퍼사드 서비스 테스트 : 성공")
    @Test
    void checkForUsername() {
        //given
        String username = "testUsername@naver.com";
        given(userService.isDuplicatedUsername(anyString())).willReturn(true);

        //when
        UsernameCheckResponse response = userFacadeService.checkForUsername(username);

        //then
        then(userService).should(times(1)).isDuplicatedUsername(username);
        assertThat(response.getIsDuplicatedUsername()).isTrue();
    }

    @DisplayName("유저 회원가입 퍼사드 서비스 테스트 : 성공")
    @Test
    void joinUser() {
        //given
        String username = "testUsername@naver.com";
        String password = "testPassword";
        UserJoinRequest request = new UserJoinRequest(username, password);

        //when
        userFacadeService.joinUser(request);

        //then
        then(userService).should(times(1)).checkForUsername(anyString());
        then(userService).should(times(1)).saveUser(userCaptor.capture());
        then(userMailCodeService).should(times(1)).saveMailCode(eq(username), anyString());
        then(mailService).should(times(1)).sendMail(any(MailMessage.class));
        assertThat(userCaptor.getValue().getUsername()).isEqualTo(username);
        assertThat(userCaptor.getValue().getPassword()).startsWith("{bcrypt}");
    }

    @DisplayName("메일 인증 퍼사드 서비스 테스트 : 실패 - 메일 코드 불일치")
    @Test
    void authenticateMail_notMatchMailCode() {
        //given
        String email = "testUsername@naver.com";
        String code = "testCode";
        willThrow(new BusinessException(EXPIRED_MAIL_AUTH))
            .given(userMailCodeService).validateMailCode(anyString(), anyString());

        //when, then
        assertThatThrownBy(() -> userFacadeService.authenticateMail(email, code))
            .isInstanceOf(BusinessTemplateException.class)
            .extracting("message", "template")
            .containsExactly(EXPIRED_MAIL_AUTH.getMessage(), "mail-auth-fail");
    }

    @DisplayName("인증용 메일 재전송 퍼사드 서비스 테스트 : 실패 - 이미 인증된 메일")
    @Test
    void resendMailAuth_alreadyAuthenticatedMail() {
        //given
        String email = "testUsername@naver.com";
        willThrow(new BusinessException(ALREADY_AUTHENTICATED_MAIL))
            .given(userService).getUserWithUnauthenticatedMail(anyString());

        //when, then
        assertThatThrownBy(() -> userFacadeService.resendMailAuth(email))
            .isInstanceOf(BusinessTemplateException.class)
            .extracting("message", "template")
            .containsExactly(ALREADY_AUTHENTICATED_MAIL.getMessage(), "mail-auth-resend-fail");
    }
}
