package com.lab2.user.service;

import com.lab2.user.dto.AuthenticationRequest;
import com.lab2.user.dto.AuthenticationResponse;
import com.lab2.user.entity.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserDetailsServiceImpl userDetailsService;

    public Mono<AuthenticationResponse> authenticate(AuthenticationRequest authenticationRequest) {
        Mono<Authentication> authenticationMono = Mono.fromCallable(() -> authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticationRequest.getUsername(),
                        authenticationRequest.getPassword()
                )));
        return Mono.fromCallable(() -> userDetailsService.loadUserByUsername(authenticationRequest.getUsername()))
                .zipWith(authenticationMono)
                .flatMap(tuple -> {
                    UserDetails userDetails = tuple.getT1();
                    if (userDetails == null) return Mono.error(new EntityNotFoundException());
                    User user = (User) userDetails;
                    String token = jwtService.generateToken(user);
                    return Mono.just(AuthenticationResponse.builder()
                            .userId(user.getId())
                            .username(user.getUsername())
                            .token(token)
                            .build());
                })
                .subscribeOn(Schedulers.boundedElastic());
    }
}
