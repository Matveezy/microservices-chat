package com.lab2.user.controller;

import com.lab2.user.dto.CreateUserDto;
import com.lab2.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody @Valid CreateUserDto request) {
        return new ResponseEntity<>(userService.create(request), HttpStatus.CREATED);
    }
}
