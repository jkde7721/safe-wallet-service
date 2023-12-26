package com.wanted.safewallet.domain.user.business.facade;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.wanted.safewallet.domain.user.business.mapper.UserMapper;
import com.wanted.safewallet.domain.user.business.service.UserMailCodeService;
import com.wanted.safewallet.domain.user.business.service.UserService;
import com.wanted.safewallet.domain.user.persistence.entity.User;
import com.wanted.safewallet.domain.user.web.dto.request.UserJoinRequest;
import com.wanted.safewallet.domain.user.web.dto.response.UsernameCheckResponse;
import com.wanted.safewallet.global.config.PasswordEncoderConfig;
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
        String username = "testUsername";
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
        String username = "testUsername";
        String password = "testPassword";
        UserJoinRequest request = new UserJoinRequest(username, password);

        //when
        userFacadeService.joinUser(request);

        //then
        then(userService).should(times(1)).checkForUsername(anyString());
        then(userService).should(times(1)).saveUser(userCaptor.capture());
        assertThat(userCaptor.getValue().getUsername()).isEqualTo(username);
        assertThat(userCaptor.getValue().getPassword()).startsWith("{bcrypt}");
    }
}
