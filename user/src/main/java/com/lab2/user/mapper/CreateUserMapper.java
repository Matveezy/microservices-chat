package com.lab2.user.mapper;

import com.lab2.user.dto.CreateUserDto;
import com.lab2.user.entity.Role;
import com.lab2.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreateUserMapper implements Mapper<CreateUserDto, User> {

    private final PasswordEncoder passwordEncoder;

    @Override
    public CreateUserDto mapToDto(User entity) {
        return null;
    }

    @Override
    public User mapToEntity(CreateUserDto dto) {
        return User.builder()
                .username(dto.getUsername())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .role(Role.valueOf(dto.getRole()))
                .build();
    }
}
