package com.wanted.safewallet.domain.user.business.service;

import static com.wanted.safewallet.global.exception.ErrorCode.ALREADY_EXISTS_USERNAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.wanted.safewallet.domain.user.business.mapper.UserMapper;
import com.wanted.safewallet.domain.user.persistence.entity.User;
import com.wanted.safewallet.domain.user.persistence.repository.UserRepository;
import com.wanted.safewallet.domain.user.web.dto.request.UserJoinRequest;
import com.wanted.safewallet.domain.user.web.dto.response.UsernameCheckResponse;
import com.wanted.safewallet.global.config.PasswordEncoderConfig;
import com.wanted.safewallet.global.exception.BusinessException;
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
class UserServiceTest {

    UserService userService;

    @Spy
    UserMapper userMapper;

    @Mock
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Captor
    ArgumentCaptor<User> userCaptor;

    @BeforeEach
    void init() {
        userService = new UserService(userMapper, userRepository, passwordEncoder);
    }

    @DisplayName("유저 계정명 중복 여부 확인 테스트 : 성공")
    @Test
    void isDuplicatedUsername() {
        //given
        String username = "testUsername";
        given(userRepository.existsByUsername(anyString())).willReturn(true);

        //when
        UsernameCheckResponse response = userService.isDuplicatedUsername(username);

        //then
        then(userRepository).should(times(1)).existsByUsername(username);
        assertThat(response.getIsDuplicatedUsername()).isTrue();
    }

    @DisplayName("유저 회원가입 서비스 테스트 : 성공")
    @Test
    void joinUser() {
        //given
        String username = "testUsername";
        UserJoinRequest request = new UserJoinRequest(username, "testPassword");
        given(userRepository.existsByUsername(anyString())).willReturn(false);

        //when
        userService.joinUser(request);

        //then
        then(userRepository).should(times(1)).existsByUsername(anyString());
        then(userRepository).should(times(1)).save(userCaptor.capture());
        assertThat(userCaptor.getValue().getUsername()).isEqualTo(username);
        assertThat(userCaptor.getValue().getPassword()).startsWith("{bcrypt}");
    }

    @DisplayName("유저 계정명 중복 검사 테스트 : 실패")
    @Test
    void checkForUsername_fail() {
        //given
        String username = "testUsername";
        given(userRepository.existsByUsername(anyString())).willReturn(true);

        //when, then
        assertThatThrownBy(() -> userService.checkForUsername(username))
            .isInstanceOf(BusinessException.class)
            .extracting("errorCode").isEqualTo(ALREADY_EXISTS_USERNAME);
        then(userRepository).should(times(1)).existsByUsername(username);
    }
}
