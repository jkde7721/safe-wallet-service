package com.wanted.safewallet.domain.user.business.facade;

import com.wanted.safewallet.domain.user.business.mapper.UserMapper;
import com.wanted.safewallet.domain.user.business.service.UserService;
import com.wanted.safewallet.domain.user.web.dto.request.UserJoinRequest;
import com.wanted.safewallet.domain.user.web.dto.response.UsernameCheckResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserFacadeService {

    private final UserMapper userMapper;
    private final UserService userService;

    public UsernameCheckResponse checkForUsername(String username) {
        boolean isDuplicatedUsername = userService.isDuplicatedUsername(username);
        return userMapper.toDto(isDuplicatedUsername);
    }

    @Transactional
    public void joinUser(UserJoinRequest request) {
        userService.joinUser(request.getUsername(), request.getPassword());
    }
}
