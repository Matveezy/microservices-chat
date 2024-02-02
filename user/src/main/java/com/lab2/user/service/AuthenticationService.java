package com.lab2.user.service;

import com.lab2.user.dto.AuthenticationRequest;
import com.lab2.user.dto.AuthenticationResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;

    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticationRequest.getUsername(),
                        authenticationRequest.getPassword()
                ));
        return userService.findUserByUsername(authenticationRequest.getUsername())
                .map(user -> {
                    String token = jwtService.generateToken(user);
                    return AuthenticationResponse.builder()
                            .userId(user.getId())
                            .username(user.getUsername())
                            .token(token)
                            .build();
                })
                .orElseThrow(EntityNotFoundException::new);
    }
}
