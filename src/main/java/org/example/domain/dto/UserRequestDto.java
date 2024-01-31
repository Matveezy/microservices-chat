package org.example.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.domain.model.Role;
import org.example.domain.model.User;

import java.util.Collections;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "User request")
public class UserRequestDto {

    @Schema(description = "Username", example = "Johny")
    @Size(min = 4, max = 50, message = "User name must contain from 4 to 50 characters")
    @NotBlank(message = "Username cannot be empty")
    private String username;

    @Schema(description = "Email", example = "jondoe@gmail.com")
    @Size(min = 6, max = 255, message = "Password must contain from 6 to 255 characters")
    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Email must be formatted like this: user@example.com")
    private String email;

    @Schema(description = "Password", example = "my_1secret1_password")
    @Size(min = 4, max = 255, message = "Password must contain from 4 to 255 characters")
    @NotBlank(message = "Email cannot be empty")
    private String password;

    @Schema(description = "Role", example = "ROLE_USER")
    @Size(min = 4, max = 255, message = "Password must contain from 4 to 255 characters")
    @NotBlank(message = "Role cannot be empty")
    @Pattern(regexp = "ROLE_[A-Z]+")
    private String role;

    public UserRequestDto(User user) {
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.email = user.getEmail();
        this.role = user.getRole().toString();
    }

    public User toUserEntity(String encodedPassword) {
        return this.toUserEntity(null, encodedPassword);
    }

    public User toUserEntity(Long id, String encodedPassword) {
        return new User(
                id,
                this.username,
                encodedPassword,
                this.email,
                Role.valueOf(this.role),
                Collections.emptyList(),
                Collections.emptyList());
    }
}