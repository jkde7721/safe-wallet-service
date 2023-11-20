package com.wanted.safewallet.domain.user.business.service;

import static com.wanted.safewallet.global.exception.ErrorCode.ALREADY_EXISTS_USERNAME;

import com.wanted.safewallet.domain.user.business.mapper.UserMapper;
import com.wanted.safewallet.domain.user.persistence.entity.User;
import com.wanted.safewallet.domain.user.persistence.repository.UserRepository;
import com.wanted.safewallet.domain.user.web.dto.request.UserJoinRequestDto;
import com.wanted.safewallet.domain.user.web.dto.response.UsernameCheckResponseDto;
import com.wanted.safewallet.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UsernameCheckResponseDto isDuplicatedUsername(String username) {
        boolean isDuplicatedUsername = userRepository.existsByUsername(username);
        return userMapper.toDto(isDuplicatedUsername);
    }

    @Transactional
    public void joinUser(UserJoinRequestDto requestDto) {
        checkForUsername(requestDto.getUsername());
        String username = requestDto.getUsername();
        String password = passwordEncoder.encode(requestDto.getPassword());
        User user = User.builder().username(username).password(password).build();
        userRepository.save(user);
    }

    public void checkForUsername(String username) {
        if (userRepository.existsByUsername(username)) {
            throw new BusinessException(ALREADY_EXISTS_USERNAME);
        }
    }
}
