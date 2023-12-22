package com.wanted.safewallet.domain.user.business.facade;

import com.wanted.safewallet.domain.user.business.mapper.UserMapper;
import com.wanted.safewallet.domain.user.business.service.UserService;
import com.wanted.safewallet.domain.user.persistence.entity.User;
import com.wanted.safewallet.domain.user.web.dto.request.UserJoinRequest;
import com.wanted.safewallet.domain.user.web.dto.response.UsernameCheckResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserFacadeService {

    private final UserMapper userMapper;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UsernameCheckResponse checkForUsername(String username) {
        boolean isDuplicatedUsername = userService.isDuplicatedUsername(username);
        return userMapper.toResponse(isDuplicatedUsername);
    }

    @Transactional
    public void joinUser(UserJoinRequest request) {
        userService.checkForUsername(request.getUsername());
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        User user = userMapper.toEntity(request, encodedPassword);
        userService.saveUser(user);
    }

    @Transactional
    public void withdrawUser(String userId) {
        User user = userService.getUser(userId);
        userService.deleteUser(user);
    }
}
