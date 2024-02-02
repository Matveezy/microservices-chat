package com.lab2.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateUserDto {

    @Size(min = 4, max = 50, message = "User name must contain from 4 to 50 characters")
    private String username;

    @Size(min = 6, max = 255, message = "Password must contain from 6 to 255 characters")
    @Email(message = "Email must be formatted like this: user@example.com")
    private String email;

    @Size(min = 4, max = 255, message = "Password must contain from 4 to 255 characters")
    private String password;

    @Size(min = 4, max = 255, message = "Password must contain from 4 to 255 characters")
    @Pattern(regexp = "ROLE_[A-Z]+")
    private String role;
}
