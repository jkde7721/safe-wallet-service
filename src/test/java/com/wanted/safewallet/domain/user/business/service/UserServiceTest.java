package com.wanted.safewallet.domain.user.business.service;

import static com.wanted.safewallet.domain.user.persistence.entity.Role.ANONYMOUS;
import static com.wanted.safewallet.domain.user.persistence.entity.Role.USER;
import static com.wanted.safewallet.global.exception.ErrorCode.ALREADY_AUTHENTICATED_MAIL;
import static com.wanted.safewallet.global.exception.ErrorCode.ALREADY_EXISTS_USERNAME;
import static com.wanted.safewallet.global.exception.ErrorCode.NOT_FOUND_USER;
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
import java.time.LocalDateTime;
import java.util.Optional;
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

    @DisplayName("유저 저장 서비스 테스트 : 실패 - 인코딩되지 않은 비밀번호")
    @Test
    void saveUser() {
        //given
        String password = "plainPassword";
        User user = anUser().password(password).build();

        //when, then
        assertThatThrownBy(() -> userService.saveUser(user))
            .isInstanceOf(BusinessException.class)
            .extracting("errorCode").isEqualTo(PASSWORD_ENCODING_ERROR);
    }

    @DisplayName("메일 인증에 따른 유저 권한 업그레이드 서비스 테스트 : 성공")
    @Test
    void upgradeToUserRole() {
        //given
        User user = anUser().role(ANONYMOUS).build();
        given(userRepository.findByUsername(anyString())).willReturn(Optional.of(user));

        //when
        userService.upgradeToUserRole(user.getUsername());

        //then
        then(userRepository).should(times(1)).findByUsername(anyString());
        assertThat(user.getRole()).isEqualTo(USER);
    }

    @DisplayName("유저 삭제 서비스 테스트 : 성공 - Soft Delete 수행")
    @Test
    void deleteUser() {
        //given
        User user = anUser().build();

        //when
        userService.deleteUser(user);

        //when
        assertThat(user.getDeleted()).isTrue();
        assertThat(user.getDeletedDate()).isNotNull();
    }

    @DisplayName("기존 비활성 계정 활성화한 후 유저 조회 서비스 테스트 - 비활성 계정 없음")
    @Test
    void getRestoredUserByUsername_noInactiveUser() {
        //given
        String username = "testUsername@naver.com";
        given(userRepository.findInactiveUserByUsername(anyString())).willReturn(Optional.empty());

        //when
        Optional<User> restoredUser = userService.getRestoredUserByUsername(username);

        //then
        assertThat(restoredUser).isEmpty();
    }

    @DisplayName("기존 비활성 계정 활성화한 후 유저 조회 서비스 테스트 - 비활성 계정 복원")
    @Test
    void getRestoredUserByUsername_withRestoredUser() {
        //given
        User inactiveUser = anUser().deleted(true).deletedDate(LocalDateTime.now()).build();
        String username = "testUsername@naver.com";
        given(userRepository.findInactiveUserByUsername(anyString())).willReturn(Optional.of(inactiveUser));

        //when
         User restoredUser = userService.getRestoredUserByUsername(username).orElse(null);

        //then
        assertThat(restoredUser).isNotNull();
        assertThat(restoredUser.getDeleted()).isFalse();
        assertThat(restoredUser.getDeletedDate()).isNull();
    }

    @DisplayName("메일 미인증 유저 조회 서비스 테스트 : 실패 - 이미 인증된 메일")
    @Test
    void getUserWithUnauthenticatedMail_fail() {
        //given
        User user = anUser().role(USER).build();
        given(userRepository.findByUsername(anyString())).willReturn(Optional.of(user));

        //when, then
        assertThatThrownBy(() -> userService.getUserWithUnauthenticatedMail(user.getUsername()))
            .isInstanceOf(BusinessException.class)
            .extracting("errorCode").isEqualTo(ALREADY_AUTHENTICATED_MAIL);
    }

    @DisplayName("유저 계정명 중복 검사 테스트 : 실패")
    @Test
    void checkForUsername_fail() {
        //given
        String username = "testUsername@naver.com";
        given(userRepository.existsByUsername(anyString())).willReturn(true);

        //when, then
        assertThatThrownBy(() -> userService.checkForUsername(username))
            .isInstanceOf(BusinessException.class)
            .extracting("errorCode").isEqualTo(ALREADY_EXISTS_USERNAME);
        then(userRepository).should(times(1)).existsByUsername(username);
    }

    @DisplayName("아이디로 유저 조회 서비스 테스트 : 실패 - 해당 유저 없음")
    @Test
    void getUser_fail() {
        //given
        String userId = "testUserId";
        given(userRepository.findById(anyString())).willReturn(Optional.empty());

        //when, then
        assertThatThrownBy(() -> userService.getUser(userId))
            .isInstanceOf(BusinessException.class)
            .extracting("errorCode").isEqualTo(NOT_FOUND_USER);
    }

    @DisplayName("계정명으로 유저 조회 서비스 테스트 : 실패 - 해당 유저 없음")
    @Test
    void getUserByUsername_fail() {
        //given
        String username = "testUsername@naver.com";
        given(userRepository.findByUsername(anyString())).willReturn(Optional.empty());

        //when, then
        assertThatThrownBy(() -> userService.getUserByUsername(username))
            .isInstanceOf(BusinessException.class)
            .extracting("errorCode").isEqualTo(NOT_FOUND_USER);
    }
}
