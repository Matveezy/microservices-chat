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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final CreateUserMapper createUserMapper;
    private final UserRepository userRepository;
    private final UserReadMapper userReadMapper;
    private final PasswordEncoder passwordEncoder;

    public Mono<UserReadDto> findById(Long userId) {
        return Mono.fromCallable(() -> userRepository.findById(userId))
                .flatMap(optional -> {
                    if (optional.isEmpty())
                        return Mono.error(new EntityNotFoundException("User with id " + userId + " doesn't exist!"));
                    return Mono.just(optional.get());
                })
                .map(userReadMapper::mapToDto)
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Transactional
    public Mono<UserReadDto> create(CreateUserDto createUserDto) {
        User entityToSave = createUserMapper.mapToEntity(createUserDto);
        return Mono.fromCallable(() -> userRepository.save(entityToSave))
                .map(userReadMapper::mapToDto)
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Transactional
    public Mono<UserReadDto> update(Long userId, UpdateUserDto updateUserDto) {
        return Mono.fromCallable(() -> userRepository.findById(userId))
                .flatMap(userOptional -> {
                    if (userOptional.isEmpty())
                        return Mono.error(new EntityNotFoundException("User with id " + userId + " doesn't exist!"));
                    return Mono.just(userOptional.get());
                }).map(user -> {
                    if (updateUserDto.getUsername() != null) user.setUsername(updateUserDto.getUsername());
                    if (updateUserDto.getEmail() != null) user.setEmail(updateUserDto.getEmail());
                    if (updateUserDto.getPassword() != null)
                        user.setPassword(passwordEncoder.encode(updateUserDto.getPassword()));
                    if (updateUserDto.getRole() != null) user.setRole(Role.valueOf(updateUserDto.getRole()));
                    return userRepository.save(user);
                }).map(userReadMapper::mapToDto)
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Transactional
    public Mono<User> delete(Long userId) {
        return Mono.fromCallable(() -> userRepository.findById(userId))
                .flatMap(userOptional -> {
                    if (userOptional.isEmpty())
                        return Mono.error(new EntityNotFoundException("User with id " + userId + " doesn't exist!"));
                    return Mono.just(userOptional.get());
                })
                .map(user -> {
                    userRepository.delete(user);
                    return user;
                })
                .subscribeOn(Schedulers.boundedElastic());
    }

}
