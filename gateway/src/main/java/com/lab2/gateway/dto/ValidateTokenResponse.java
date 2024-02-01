package com.lab2.gateway.dto;

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
