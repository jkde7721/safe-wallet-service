package com.wanted.safewallet.domain.user.business.facade;

import static java.util.UUID.randomUUID;

import com.wanted.safewallet.domain.user.business.mapper.UserMapper;
import com.wanted.safewallet.domain.user.business.service.UserMailCodeService;
import com.wanted.safewallet.domain.user.business.service.UserService;
import com.wanted.safewallet.domain.user.persistence.entity.User;
import com.wanted.safewallet.domain.user.web.dto.request.UserJoinRequest;
import com.wanted.safewallet.domain.user.web.dto.response.UsernameCheckResponse;
import com.wanted.safewallet.global.mail.dto.MailMessage;
import com.wanted.safewallet.global.mail.service.MailService;
import java.util.Map;
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
    private final UserMailCodeService userMailCodeService;
    private final MailService mailService;
    private final PasswordEncoder passwordEncoder;

    private static final String MAIL_AUTH_SUBJECT = "[Safe Wallet] 회원가입을 위해 메일을 인증해 주세요.";
    private static final String MAIL_AUTH_CODE_NAME = "code";
    private static final String MAIL_AUTH_EMAIL_NAME = "email";
    private static final String MAIL_AUTH_TEMPLATE = "mail-auth";

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
        processMailAuthMessage(user.getUsername());
    }

    @Transactional
    public void deactivateUser(String userId) {
        User user = userService.getUser(userId);
        userService.deleteUser(user);
    }

    @Transactional
    public void authenticateMail(String email, String code) {
        userMailCodeService.validateMailCode(email, code);
        userService.upgradeToUserRole(email);
    }

    public void resendMailAuth(String email) {
        userService.getUserWithUnauthenticatedMail(email);
        processMailAuthMessage(email);
    }

    private void processMailAuthMessage(String toEmail) {
        String code = randomUUID().toString();
        MailMessage mailAuthMessage = MailMessage.builder()
            .to(toEmail).subject(MAIL_AUTH_SUBJECT)
            .variables(Map.of(MAIL_AUTH_EMAIL_NAME, toEmail, MAIL_AUTH_CODE_NAME, code))
            .template(MAIL_AUTH_TEMPLATE).build();
        userMailCodeService.saveMailCode(toEmail, code);
        mailService.sendMail(mailAuthMessage); //비동기
    }
}
