package com.lab2.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationRequest {

    @Size(min = 4, max = 50, message = "User name must contain from 4 to 50 characters")
    @NotBlank(message = "Username cannot be empty")
    private String username;

    @Size(min = 4, max = 255, message = "Password must contain from 4 to 255 characters")
    @NotBlank(message = "Password cannot be empty")
    private String password;
}

