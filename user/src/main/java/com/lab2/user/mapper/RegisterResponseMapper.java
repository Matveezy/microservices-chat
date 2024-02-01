package com.lab2.user.mapper;

import com.lab2.user.dto.RegisterResponseDto;
import com.lab2.user.entity.User;
import org.springframework.stereotype.Component;

@Component
public class RegisterResponseMapper implements Mapper<RegisterResponseDto, User> {

    @Override
    public RegisterResponseDto mapToDto(User entity) {
        return RegisterResponseDto.builder()
                .id(entity.getId())
                .email(entity.getEmail())
                .username(entity.getUsername())
                .email(entity.getRole().toString())
                .build();
    }
}
