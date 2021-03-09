package com.mikhailkarpov.todoclient.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@RestController
public class UserController {

    @GetMapping("/users/me")
    public Map<String, Object> getCurrentUserAttributes(@AuthenticationPrincipal OAuth2User user) {

        return Collections.singletonMap("name", user.getName());
    }
}
