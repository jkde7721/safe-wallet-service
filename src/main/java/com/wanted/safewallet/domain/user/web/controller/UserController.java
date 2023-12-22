package com.wanted.safewallet.domain.user.web.controller;

import com.wanted.safewallet.domain.user.business.facade.UserFacadeService;
import com.wanted.safewallet.domain.user.web.dto.request.UserJoinRequest;
import com.wanted.safewallet.domain.user.web.dto.response.UsernameCheckResponse;
import com.wanted.safewallet.global.auth.annotation.CurrentUserId;
import com.wanted.safewallet.global.dto.response.aop.CommonResponseContent;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CommonResponseContent
@RequiredArgsConstructor
@RequestMapping("/api/users")
@RestController
public class UserController {

    private final UserFacadeService userFacadeService;

    @GetMapping("/{username}")
    public UsernameCheckResponse checkForUsername(@PathVariable String username) {
        return userFacadeService.checkForUsername(username);
    }

    @CommonResponseContent(status = HttpStatus.CREATED)
    @PostMapping
    public void joinUser(@RequestBody @Valid UserJoinRequest request) {
        userFacadeService.joinUser(request);
    }

    @DeleteMapping
    public void withdrawUser(@CurrentUserId String userId) {
        userFacadeService.withdrawUser(userId);
    }
}
