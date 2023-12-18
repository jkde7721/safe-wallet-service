package com.wanted.safewallet.domain.user.business.facade;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.wanted.safewallet.domain.user.business.mapper.UserMapper;
import com.wanted.safewallet.domain.user.business.service.UserService;
import com.wanted.safewallet.domain.user.web.dto.response.UsernameCheckResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserFacadeServiceTest {

    @InjectMocks
    UserFacadeService userFacadeService;

    @Spy
    UserMapper userMapper;

    @Mock
    UserService userService;

    @DisplayName("유저 계정명 중복 여부 확인 테스트 : 성공")
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
}
