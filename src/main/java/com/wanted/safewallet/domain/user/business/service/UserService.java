package com.wanted.safewallet.domain.user.business.service;

import static com.wanted.safewallet.domain.user.persistence.entity.Role.ANONYMOUS;
import static com.wanted.safewallet.domain.user.persistence.entity.Role.USER;
import static com.wanted.safewallet.global.exception.ErrorCode.ALREADY_AUTHENTICATED_MAIL;
import static com.wanted.safewallet.global.exception.ErrorCode.ALREADY_EXISTS_USERNAME;
import static com.wanted.safewallet.global.exception.ErrorCode.NOT_FOUND_USER;
import static com.wanted.safewallet.global.exception.ErrorCode.PASSWORD_ENCODING_ERROR;

import com.wanted.safewallet.domain.user.persistence.entity.User;
import com.wanted.safewallet.domain.user.persistence.repository.UserRepository;
import com.wanted.safewallet.global.exception.BusinessException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserService {

    private final UserRepository userRepository;
    private static final String ENCODED_PASSWORD_PREFIX = "{bcrypt}";
    private static final int EXPIRY_MONTH = 3;

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

    @Transactional
    public void upgradeToUserRole(String username) {
        User user = getUserWithUnauthenticatedMail(username);
        user.updateRole(USER);
    }

    @Transactional
    public void deleteUser(User user) {
        user.softDelete();
    }

    @Transactional
    public Optional<User> getRestoredUserByUsername(String username) {
        Optional<User> optionalUser = userRepository.findInactiveUserByUsername(username);
        if (optionalUser.isEmpty() || !isWithinExpiryDate(optionalUser.get().getDeletedDate())) {
            return Optional.empty();
        }
        User inactiveUser = optionalUser.get();
        inactiveUser.restore();
        return Optional.of(inactiveUser);
    }

    @Transactional
    public void withdrawByIds(List<String> ids) {
        userRepository.deleteAllByIdIn(ids);
    }

    public List<String> getWithdrawnUserIds() {
        LocalDateTime minDeletedDate = LocalDate.now().minusMonths(EXPIRY_MONTH).atStartOfDay();
        return userRepository.findIdsByDeletedAndDeletedDate(minDeletedDate);
    }

    public User getUserWithUnauthenticatedMail(String username) {
        User user = getUserByUsername(username);
        if (user.getRole() != ANONYMOUS) {
            throw new BusinessException(ALREADY_AUTHENTICATED_MAIL);
        }
        return user;
    }

    public void checkForExistingMailAuth(String username) {
        getUserWithUnauthenticatedMail(username);
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

    public Optional<User> getActiveUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    private boolean isWithinExpiryDate(LocalDateTime deletedDate) {
        LocalDate expiryDate = deletedDate.plusMonths(EXPIRY_MONTH).toLocalDate();
        LocalDate now = LocalDate.now();
        return now.isBefore(expiryDate) || now.isEqual(expiryDate);
    }
}
