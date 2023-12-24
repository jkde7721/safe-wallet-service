package com.wanted.safewallet.domain.auth.business.service;

import static com.wanted.safewallet.domain.user.persistence.entity.Role.ANONYMOUS;
import static com.wanted.safewallet.utils.Fixtures.anUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.wanted.safewallet.domain.auth.business.dto.CustomUserDetails;
import com.wanted.safewallet.domain.user.business.service.UserService;
import com.wanted.safewallet.domain.user.persistence.entity.User;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @InjectMocks
    CustomUserDetailsService customUserDetailsService;

    @Mock
    UserService userService;

    @DisplayName("계정명으로 유저 조회 테스트 : 성공 - 활성화 계정 조회")
    @Test
    void loadUserByUsername_withActiveUser() {
        //given
        User user = anUser().build();
        given(userService.getActiveUserByUsername(anyString())).willReturn(Optional.of(user));

        //when
        CustomUserDetails userDetails = (CustomUserDetails) customUserDetailsService.loadUserByUsername(user.getUsername());

        //then
        assertThat(userDetails.getUserId()).isEqualTo(user.getId());
        assertThat(userDetails.getUsername()).isEqualTo(user.getUsername());
        assertThat(userDetails.getPassword()).isEqualTo(user.getPassword());
        then(userService).should(times(1)).getActiveUserByUsername(anyString());
        then(userService).should(times(0)).getRestoredUserByUsername(anyString());
    }

    @DisplayName("계정명으로 유저 조회 테스트 : 성공 - 비활성화 계정 조회")
    @Test
    void loadUserByUsername_withInactiveUser() {
        //given
        User user = anUser().build();
        given(userService.getActiveUserByUsername(anyString())).willReturn(Optional.empty());
        given(userService.getRestoredUserByUsername(anyString())).willReturn(Optional.of(user));

        //when
        CustomUserDetails userDetails = (CustomUserDetails) customUserDetailsService.loadUserByUsername(user.getUsername());

        //then
        assertThat(userDetails.getUserId()).isEqualTo(user.getId());
        assertThat(userDetails.getUsername()).isEqualTo(user.getUsername());
        assertThat(userDetails.getPassword()).isEqualTo(user.getPassword());
        then(userService).should(times(1)).getActiveUserByUsername(anyString());
        then(userService).should(times(1)).getRestoredUserByUsername(anyString());
    }

    @DisplayName("계정명으로 유저 조회 테스트 : 실패 - 해당 유저 없음")
    @Test
    void loadUserByUsername_noUser() {
        //given
        String username = "testUsername";
        given(userService.getActiveUserByUsername(anyString())).willReturn(Optional.empty());
        given(userService.getRestoredUserByUsername(anyString())).willReturn(Optional.empty());

        //when, then
        assertThatThrownBy(() -> customUserDetailsService.loadUserByUsername(username))
            .isInstanceOf(UsernameNotFoundException.class)
            .hasMessage("잘못된 계정명입니다.");
    }

    @DisplayName("계정명으로 유저 조회 테스트 : 실패 - 이메일 인증 미완료")
    @Test
    void loadUserByUsername_noEmailConfirm() {
        //given
        User user = anUser().role(ANONYMOUS).build();
        given(userService.getActiveUserByUsername(anyString())).willReturn(Optional.of(user));

        //when, then
        assertThatThrownBy(() -> customUserDetailsService.loadUserByUsername(user.getUsername()))
            .isInstanceOf(UsernameNotFoundException.class)
            .hasMessage("이메일 인증이 완료되지 않았습니다.");
    }
}
