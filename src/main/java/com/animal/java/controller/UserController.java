package com.animal.java.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @GetMapping
    public Map<String,  Object> user(@AuthenticationPrincipal Jwt jwt){
        String name = jwt.getClaimAsString("name");
        if (name == null) {
            name = jwt.getClaimAsString("preferred_username");
        }
        return Collections.singletonMap("name", name);
    }
}
