package com.lab2.user.controller;

import com.lab2.user.dto.ValidateTokenResponse;
import com.lab2.user.entity.Role;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/token")
public class TokenController {

    @GetMapping("/validate")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ValidateTokenResponse> validateToken(HttpServletRequest httpServletRequest) {
        System.out.println("VALIDATE TOKEN");
        String username = (String) httpServletRequest.getAttribute("username");
        System.out.println("USERNAME");
        if (username == null) return ResponseEntity.ok(ValidateTokenResponse.builder().isAuthenticated(false).build());
        String roleString = httpServletRequest.getAttribute("authority").toString().replace("[", "").replace("]", "");
        Role authority = Role.valueOf(roleString);
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
