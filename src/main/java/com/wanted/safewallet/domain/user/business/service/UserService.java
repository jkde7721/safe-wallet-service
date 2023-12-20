package com.wanted.safewallet.domain.user.business.service;

import static com.wanted.safewallet.global.exception.ErrorCode.ALREADY_EXISTS_USERNAME;
import static com.wanted.safewallet.global.exception.ErrorCode.NOT_FOUND_USER;
import static com.wanted.safewallet.global.exception.ErrorCode.PASSWORD_ENCODING_ERROR;

import com.wanted.safewallet.domain.user.persistence.entity.User;
import com.wanted.safewallet.domain.user.persistence.repository.UserRepository;
import com.wanted.safewallet.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserService {

    private final UserRepository userRepository;
    private static final String ENCODED_PASSWORD_PREFIX = "{bcrypt}";

    public boolean isDuplicatedUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Transactional
    public void saveUser(User user) {
        if (!user.getPassword().startsWith(ENCODED_PASSWORD_PREFIX)) {
            throw new BusinessException(PASSWORD_ENCODING_ERROR);
        }
        userRepository.save(user);
    }

    public void checkForUsername(String username) {
        if (userRepository.existsByUsername(username)) {
            throw new BusinessException(ALREADY_EXISTS_USERNAME);
        }
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new BusinessException(NOT_FOUND_USER));
    }
}
