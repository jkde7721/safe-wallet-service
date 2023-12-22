package com.wanted.safewallet.domain.user.business.service;

import static com.wanted.safewallet.global.exception.ErrorCode.ALREADY_EXISTS_USERNAME;
import static com.wanted.safewallet.global.exception.ErrorCode.PASSWORD_ENCODING_ERROR;
import static com.wanted.safewallet.utils.Fixtures.anUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.wanted.safewallet.domain.user.persistence.entity.User;
import com.wanted.safewallet.domain.user.persistence.repository.UserRepository;
import com.wanted.safewallet.global.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    UserService userService;

    @Mock
    UserRepository userRepository;

    @DisplayName("유저 회원가입 서비스 테스트 : 실패 - 인코딩되지 않은 비밀번호")
    @Test
    void joinUser() {
        //given
        String password = "plainPassword";
        User user = anUser().password(password).build();

        //when, then
        assertThatThrownBy(() -> userService.saveUser(user))
            .isInstanceOf(BusinessException.class)
            .extracting("errorCode").isEqualTo(PASSWORD_ENCODING_ERROR);
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

    @DisplayName("유저 권한 String 값 반환 테스트")
    @Test
    void getCommaDelimitedAuthorities() {
        //given
        User user = anUser().build();

        //when
        String authorities = userService.getCommaDelimitedAuthorities(user);

        //then
        assertThat(authorities).isEqualTo("ROLE_USER");
    }
}
