package com.lab2.user.mapper;

import com.lab2.user.dto.UserReadDto;
import com.lab2.user.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserReadMapper implements Mapper<UserReadDto, User> {

    @Override
    public UserReadDto mapToDto(User entity) {
        return UserReadDto.builder()
                .id(entity.getId())
                .email(entity.getEmail())
                .username(entity.getUsername())
                .email(entity.getEmail())
                .role(entity.getRole().toString())
                .build();
    }
}
