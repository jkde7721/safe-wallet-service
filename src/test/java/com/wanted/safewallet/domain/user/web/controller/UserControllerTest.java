package com.wanted.safewallet.domain.user.web.controller;

import static com.wanted.safewallet.utils.JsonUtils.asJsonString;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.wanted.safewallet.domain.user.business.service.UserService;
import com.wanted.safewallet.domain.user.web.dto.request.UserJoinRequestDto;
import com.wanted.safewallet.domain.user.web.dto.response.UsernameCheckResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WithMockUser
@WebMvcTest(UserController.class)
class UserControllerTest {

    @MockBean
    UserService userService;

    @Autowired
    MockMvc mockMvc;

    @DisplayName("유저 계정명 중복 여부 확인 테스트 : 성공")
    @Test
    void checkForUsername() throws Exception {
        //given
        String username = "testUsername";
        UsernameCheckResponseDto responseDto = new UsernameCheckResponseDto(true);
        given(userService.isDuplicatedUsername(anyString())).willReturn(responseDto);

        //when, then
        mockMvc.perform(get("/api/users/" + username)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.isDuplicatedUsername").isBoolean())
            .andDo(print());
        then(userService).should(times(1)).isDuplicatedUsername(anyString());
    }

    @DisplayName("유저 회원가입 컨트롤러 테스트 : 실패 - 계정명 공백")
    @Test
    void joinUser_fail_username_blank() throws Exception {
        //given
        String username = "";
        String password = "hello12345!";
        UserJoinRequestDto requestDto = new UserJoinRequestDto(username, password);

        //when, then
        mockMvc.perform(post("/api/users")
                .content(asJsonString(requestDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(csrf()))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", containsString("계정명이 공백일 수 없습니다.")))
            .andDo(print());
    }

    @DisplayName("유저 회원가입 컨트롤러 테스트 : 실패 - 비밀번호 길이")
    @Test
    void joinUser_fail_password_length() throws Exception {
        //given
        String username = "testUsername";
        String password = "hi123!";
        UserJoinRequestDto requestDto = new UserJoinRequestDto(username, password);

        //when, then
        mockMvc.perform(post("/api/users")
                .content(asJsonString(requestDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(csrf()))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", containsString("비밀번호는 최소 10자 이상이어야 합니다.")))
            .andDo(print());
    }

    @DisplayName("유저 회원가입 컨트롤러 테스트 : 실패 - 비밀번호 구성 문자")
    @Test
    void joinUser_fail_password_contains_character() throws Exception {
        //given
        String username = "testUsername";
        String password = "hello12345hi";
        UserJoinRequestDto requestDto = new UserJoinRequestDto(username, password);

        //when, then
        mockMvc.perform(post("/api/users")
                .content(asJsonString(requestDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(csrf()))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", containsString("특수 문자를 1개 이상 포함해야 합니다.")))
            .andDo(print());
    }

    @DisplayName("유저 회원가입 컨트롤러 테스트 : 실패 - 한글 포함")
    @Test
    void joinUser_fail_password_contains_korean() throws Exception {
        //given
        String username = "testUsername";
        String password = "hello12345!가";
        UserJoinRequestDto requestDto = new UserJoinRequestDto(username, password);

        //when, then
        mockMvc.perform(post("/api/users")
                .content(asJsonString(requestDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(csrf()))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", containsString("비밀번호에 한글을 포함할 수 없습니다.")))
            .andDo(print());
    }

    @DisplayName("유저 회원가입 컨트롤러 테스트 : 실패 - 공백 포함")
    @Test
    void joinUser_fail_password_contains_blank() throws Exception {
        //given
        String username = "testUsername";
        String password = "hello 12345!";
        UserJoinRequestDto requestDto = new UserJoinRequestDto(username, password);

        //when, then
        mockMvc.perform(post("/api/users")
                .content(asJsonString(requestDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(csrf()))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", containsString("비밀번호에 공백을 포함할 수 없습니다.")))
            .andDo(print());
    }

    @DisplayName("유저 회원가입 컨트롤러 테스트 : 실패 - 유저 정보 포함")
    @Test
    void joinUser_fail_password_contains_username() throws Exception {
        //given
        String username = "testUsername";
        String password = "testUsername12345!";
        UserJoinRequestDto requestDto = new UserJoinRequestDto(username, password);

        //when, then
        mockMvc.perform(post("/api/users")
                .content(asJsonString(requestDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(csrf()))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", containsString("비밀번호에 계정명을 포함할 수 없습니다.")))
            .andDo(print());
    }

    @DisplayName("유저 회원가입 컨트롤러 테스트 : 실패 - 취약한 비밀번호")
    @Test
    void joinUser_fail_common_password() throws Exception {
        //given
        String username = "testUsername";
        String password = "password";
        UserJoinRequestDto requestDto = new UserJoinRequestDto(username, password);

        //when, then
        mockMvc.perform(post("/api/users")
                .content(asJsonString(requestDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(csrf()))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", containsString("취약한 비밀번호는 사용 불가합니다.")))
            .andDo(print());
    }
}
