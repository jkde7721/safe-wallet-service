package com.wanted.safewallet.domain.auth.business.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import com.wanted.safewallet.domain.auth.business.dto.response.CustomUserDetails;
import com.wanted.safewallet.domain.user.persistence.entity.User;
import com.wanted.safewallet.domain.user.persistence.repository.UserRepository;
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
    UserRepository userRepository;

    @DisplayName("계정명으로 유저 조회 테스트 : 성공")
    @Test
    void loadUserByUsername() {
        //given
        String userId = "testUserId";
        String username = "testUsername";
        String password = "testPassword";
        User user = User.builder().id(userId).username(username).password(password).build();
        given(userRepository.findByUsername(anyString())).willReturn(Optional.of(user));

        //when
        CustomUserDetails userDetails = (CustomUserDetails) customUserDetailsService.loadUserByUsername(username);

        //then
        assertThat(userDetails.getUserId()).isEqualTo(userId);
        assertThat(userDetails.getUsername()).isEqualTo(username);
        assertThat(userDetails.getPassword()).isEqualTo(password);
    }

    @DisplayName("계정명으로 유저 조회 테스트 : 실패 - 해당 유저 없음")
    @Test
    void loadUserByUsername_no_user() {
        //given
        String username = "testUsername";
        given(userRepository.findByUsername(anyString())).willReturn(Optional.empty());

        //when, then
        assertThatThrownBy(() -> customUserDetailsService.loadUserByUsername(username))
            .isInstanceOf(UsernameNotFoundException.class)
            .hasMessage("잘못된 계정명입니다.");
    }
}
