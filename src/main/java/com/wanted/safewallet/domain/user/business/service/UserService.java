package com.wanted.safewallet.domain.user.business.service;

import com.wanted.safewallet.domain.user.business.mapper.UserMapper;
import com.wanted.safewallet.domain.user.persistence.repository.UserRepository;
import com.wanted.safewallet.domain.user.web.dto.response.UsernameCheckResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;

    public UsernameCheckResponseDto isDuplicatedUsername(String username) {
        boolean isDuplicatedUsername = userRepository.existsByUsername(username);
        return userMapper.toDto(isDuplicatedUsername);
    }
}
