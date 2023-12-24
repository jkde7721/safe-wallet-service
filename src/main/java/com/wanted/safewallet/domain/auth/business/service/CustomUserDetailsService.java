package com.wanted.safewallet.domain.auth.business.service;

import static com.wanted.safewallet.domain.user.persistence.entity.Role.ANONYMOUS;

import com.wanted.safewallet.domain.auth.business.dto.CustomUserDetails;
import com.wanted.safewallet.domain.user.business.service.UserService;
import com.wanted.safewallet.domain.user.persistence.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //활성화 계정 조회 -> 비활성화 계정 조회 -> 예외 발생
        User user = userService.getActiveUserByUsername(username)
            .orElseGet(() -> userService.getRestoredUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("잘못된 계정명입니다.")));
        if (user.getRole() == ANONYMOUS) {
            throw new UsernameNotFoundException("이메일 인증이 완료되지 않았습니다.");
        }
        return new CustomUserDetails(user);
    }
}
