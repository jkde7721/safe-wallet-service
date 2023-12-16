package com.wanted.safewallet.utils.auth;

import com.wanted.safewallet.domain.auth.business.dto.CustomUserDetails;
import com.wanted.safewallet.domain.user.persistence.entity.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithMockCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUser> {

    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser customUser) {
        User user = User.builder().id(customUser.userId()).username(customUser.username())
            .password(customUser.password()).build();
        CustomUserDetails userDetails = new CustomUserDetails(user);
        UsernamePasswordAuthenticationToken authentication = UsernamePasswordAuthenticationToken
            .authenticated(userDetails.getUserId(), null, userDetails.getAuthorities());

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        return context;
    }
}
