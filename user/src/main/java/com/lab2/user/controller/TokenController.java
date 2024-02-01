package com.lab2.user.controller;

import com.google.common.net.HttpHeaders;
import com.lab2.user.dto.ValidateTokenResponse;
import com.lab2.user.entity.Role;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/token/validate")
public class TokenController {

    @GetMapping
    public ResponseEntity<ValidateTokenResponse> validateToken(HttpServletRequest httpServletRequest) {
        String username = (String) httpServletRequest.getAttribute("username");
        if (username == null) return ResponseEntity.ok(ValidateTokenResponse.builder().isAuthenticated(false).build());
        Role authority = Role.valueOf(httpServletRequest.getAttribute("authority").toString());
        Long userId = (Long) httpServletRequest.getAttribute("userId");
        return ResponseEntity.ok(
                ValidateTokenResponse.builder()
                        .userId(userId)
                        .login(username)
                        .authority(authority)
                        .isAuthenticated(true)
                        .build()
        );
    }
}
