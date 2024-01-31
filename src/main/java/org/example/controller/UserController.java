package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import org.example.domain.dto.*;
import org.example.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@RolesAllowed("ADMIN")
@RequiredArgsConstructor
@Tag(name = "User controller")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Find all users")
    @PostMapping("/all")
    public Page<UserResponseDto> getAll(@RequestBody @Valid PageableRequest pageable) {
        return userService.findAll(pageable);
    }

    @Operation(summary = "Find all users with total number of users in the header")
    @PostMapping("/all/total/in/header")
    public Page<UserResponseDto> getAllTotalInHeader(
            @RequestBody @Valid PageableRequest pageable,
            HttpServletResponse response
    ) {
        var usersPage = userService.findAll(pageable);
        response.setHeader("Total-Number-Of-Users", String.valueOf(usersPage.getTotalElements()));
        return usersPage;
    }

    @Operation(summary = "Find all users with pagination by infinite loop")
    @PostMapping("/all/by/infinite/loop")
    public List<UserResponseDto> getAllByInfiniteLoop(@RequestBody @Valid PageableRequest pageable) {
        return userService.findAllByInfiniteLoop(pageable);
    }

    @Operation(summary = "Create a user")
    @PostMapping
    public UserResponseDto create(@RequestBody @Valid UserRequestDto request) {
        return userService.create(request);
    }

    @Operation(summary = "Update a user")
    @PutMapping("/{id}")
    public UserResponseDto update(@PathParam("id") Long id, @RequestBody @Valid UserRequestDto request) {
        return userService.update(id, request);
    }

    @Operation(summary = "Delete a user")
    @DeleteMapping("/{id}")
    public UserResponseDto delete(@PathVariable("id") Long id) {
        return userService.delete(id);
    }
}

