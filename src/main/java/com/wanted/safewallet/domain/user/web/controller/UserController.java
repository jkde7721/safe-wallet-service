package com.wanted.safewallet.domain.user.web.controller;

import com.wanted.safewallet.domain.user.business.service.UserService;
import com.wanted.safewallet.domain.user.web.dto.response.UsernameCheckResponseDto;
import com.wanted.safewallet.global.dto.response.aop.CommonResponseContent;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CommonResponseContent
@RequiredArgsConstructor
@RequestMapping("/api/users")
@RestController
public class UserController {

    private final UserService userService;

    @GetMapping("/{username}")
    public UsernameCheckResponseDto checkForUsername(@PathVariable String username) {
        return userService.isDuplicatedUsername(username);
    }
}
