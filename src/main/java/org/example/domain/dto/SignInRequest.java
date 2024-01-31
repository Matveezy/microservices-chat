package org.example.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Request for authentication")
public class SignInRequest {

    @Schema(description = "Username", example = "Johny")
    @Size(min = 4, max = 50, message = "User name must contain from 4 to 50 characters")
    @NotBlank(message = "Username cannot be empty")
    private String username;

    @Schema(description = "Password", example = "1234")
    @Size(min = 4, max = 255, message = "Password must contain from 4 to 255 characters")
    @NotBlank(message = "Password cannot be empty")
    private String password;
}