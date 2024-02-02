package com.lab2.user.service;

import com.lab2.user.dto.CreateUserDto;
import com.lab2.user.dto.UpdateUserDto;
import com.lab2.user.dto.UserReadDto;
import com.lab2.user.entity.Role;
import com.lab2.user.entity.User;
import com.lab2.user.mapper.CreateUserMapper;
import com.lab2.user.mapper.UserReadMapper;
import com.lab2.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService implements UserDetailsService {

    private final CreateUserMapper createUserMapper;
    private final UserRepository userRepository;
    private final UserReadMapper userReadMapper;
    private final PasswordEncoder passwordEncoder;

    public UserReadDto findById(Long userId) {
        return userRepository.findById(userId)
                .map(userReadMapper::mapToDto)
                .orElseThrow(EntityNotFoundException::new);
    }

    @Transactional
    public UserReadDto create(CreateUserDto createUserDto) {
        User entityToSave = createUserMapper.mapToEntity(createUserDto);
        userRepository.save(entityToSave);
        return userReadMapper.mapToDto(entityToSave);
    }

    @Transactional
    public UserReadDto update(Long userId, UpdateUserDto updateUserDto) {
        Optional<User> byId = userRepository.findById(userId);
        if (byId.isEmpty()) throw new EntityNotFoundException();
        User user = byId.get();
        if (updateUserDto.getUsername() != null) user.setUsername(updateUserDto.getUsername());
        if (updateUserDto.getEmail() != null) user.setEmail(updateUserDto.getEmail());
        if (updateUserDto.getPassword() != null) user.setPassword(passwordEncoder.encode(updateUserDto.getPassword()));
        if (updateUserDto.getRole() != null) user.setRole(Role.valueOf(updateUserDto.getRole()));
        return userReadMapper.mapToDto(userRepository.save(user));
    }

    @Transactional
    public boolean delete(Long userId) {
        return userRepository.findById(userId).map(
                user -> {
                    userRepository.delete(user);
                    return true;
                }
        ).orElseThrow(EntityNotFoundException::new);
    }

    public Optional<User> findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).orElse(null);
    }
}
