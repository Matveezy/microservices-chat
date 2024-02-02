package com.lab2.user.controller;

import com.lab2.user.dto.AuthenticationRequest;
import com.lab2.user.dto.CreateUserDto;
import com.lab2.user.dto.UpdateUserDto;
import com.lab2.user.service.AuthenticationService;
import com.lab2.user.service.UserService;
import com.lab2.user.util.RequestAttributeNames;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    @PreAuthorize("permitAll()")
    public ResponseEntity<?> create(@RequestBody @Valid CreateUserDto request) {
        return new ResponseEntity<>(userService.create(request), HttpStatus.CREATED);
    }

    @GetMapping("/authenticate")
    @PreAuthorize("permitAll()")
    public ResponseEntity<?> authenticate(@RequestBody @Valid AuthenticationRequest authenticationRequest) {
        return new ResponseEntity<>(authenticationService.authenticate(authenticationRequest), HttpStatus.OK);
    }

    @PutMapping()
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<?> update(@RequestBody @Valid UpdateUserDto createUserDto, HttpServletRequest httpServletRequest) {
        Long userId = Long.valueOf(httpServletRequest.getAttribute(RequestAttributeNames.USER_ID_ATTRIBUTE_NAME).toString());
        return new ResponseEntity<>(userService.update(userId, createUserDto), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        return new ResponseEntity<>(userService.delete(id), HttpStatus.OK);
    }
}

