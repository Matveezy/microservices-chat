package org.example.domain.dto;

import lombok.Data;
import org.example.domain.model.User;

@Data
public class UserResponseDto {

    private Long id;

    private String username;

    private String email;

    private String role;

    public UserResponseDto(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.role = user.getRole().toString();
    }
}