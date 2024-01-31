package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/example")
@RequiredArgsConstructor
@Tag(name = "Examples", description = "Examples for users with different access rights")
public class ExampleController {

    @GetMapping
    @Operation(summary = "It is allowed for everybody")
    public String example() {
        return "Hello, world!";
    }

    @GetMapping("/admin")
    @Operation(summary = "It is allowed only for users with the role \"ADMIN\"")
    @RolesAllowed("ADMIN")
    public String exampleAdmin() {
        return "Hello, admin!";
    }
}