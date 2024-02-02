package com.lab2.user.dto;

import com.lab2.user.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ValidateTokenResponse {

    Long userId;
    String login;
    Role authority;
    boolean isAuthenticated;

}
