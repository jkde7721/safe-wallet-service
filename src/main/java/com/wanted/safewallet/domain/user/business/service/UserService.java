package com.wanted.safewallet.domain.user.business.service;

import static com.wanted.safewallet.global.exception.ErrorCode.ALREADY_EXISTS_USERNAME;
import static com.wanted.safewallet.global.exception.ErrorCode.NOT_FOUND_USER;
import static com.wanted.safewallet.global.exception.ErrorCode.PASSWORD_ENCODING_ERROR;

import com.wanted.safewallet.domain.user.persistence.entity.User;
import com.wanted.safewallet.domain.user.persistence.repository.UserRepository;
import com.wanted.safewallet.global.exception.BusinessException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserService {

    private final UserRepository userRepository;
    private static final String ENCODED_PASSWORD_PREFIX = "{bcrypt}";

    public boolean isDuplicatedUsername(String username) {
        return userRepository.existsByUsername(username);
    }


    public void saveUser(User user) {
        if (!user.getPassword().startsWith(ENCODED_PASSWORD_PREFIX)) {
            throw new BusinessException(PASSWORD_ENCODING_ERROR);
        }
        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(User user) {
        user.softDelete();
    }

    public void checkForUsername(String username) {
        if (userRepository.existsByUsername(username)) {
            throw new BusinessException(ALREADY_EXISTS_USERNAME);
        }
    }

    public User getUser(String userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(NOT_FOUND_USER));
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new BusinessException(NOT_FOUND_USER));
    }

    //TODO: User 엔티티에 권한 관련 필드 추가 후 실제 구현 (현재는 임의의 ROLE_USER 권한만 반환)
    public String getCommaDelimitedAuthorities(User user) {
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        return StringUtils.collectionToCommaDelimitedString(authorities);
    }
}
