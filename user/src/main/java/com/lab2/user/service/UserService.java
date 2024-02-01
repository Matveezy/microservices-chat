package com.lab2.user.service;

import com.lab2.user.dto.CreateUserDto;
import com.lab2.user.dto.RegisterResponseDto;
import com.lab2.user.entity.User;
import com.lab2.user.mapper.CreateUserMapper;
import com.lab2.user.mapper.RegisterResponseMapper;
import com.lab2.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService implements UserDetailsService {

    private final CreateUserMapper createUserMapper;
    private final UserRepository userRepository;
    private final RegisterResponseMapper registerResponseMapper;
    // TODO: 01.02.2024
    // create user
    // authenticate

    @Transactional
    public RegisterResponseDto create(CreateUserDto createUserDto) {
        User entityToSave = createUserMapper.mapToEntity(createUserDto);
        userRepository.save(entityToSave);
        return registerResponseMapper.mapToDto(entityToSave);
    }

    public Optional<User> findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).orElse(null);
    }
}
