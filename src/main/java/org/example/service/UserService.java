package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.domain.dto.PageableRequest;
import org.example.domain.dto.UserRequestDto;
import org.example.domain.dto.UserResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.example.domain.model.User;
import org.example.repository.UserRepository;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.IntStream;

import static org.example.domain.dto.PageableRequest.getPageable;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;

    private final PasswordEncoder passwordEncoder;

    public Page<UserResponseDto> findAll(PageableRequest pageableRequest) {
        var pageable = getPageable(pageableRequest, "username");
        var users = repository.findAll(pageable);
        var usersResponse = users.stream().map(UserResponseDto::new).toList();
        return new PageImpl<>(usersResponse, pageable, users.getTotalElements());
    }

    public List<UserResponseDto> findAllByInfiniteLoop(PageableRequest pageableRequest) {
        var users = repository.findAll(PageRequest.of(0, 50)).getContent();
        var firstItemPosition = pageableRequest.getPage() * pageableRequest.getSize();
        return IntStream.range(firstItemPosition, firstItemPosition + pageableRequest.getSize())
                .mapToObj((itemPosition) -> users.get(itemPosition % users.size()))
                .map(UserResponseDto::new)
                .toList();
    }

    public UserResponseDto create(UserRequestDto user) {
        if (repository.existsByUsername(user.getUsername())) {
            throw new ResponseStatusException(BAD_REQUEST, "A user with this username already exists");
        }
        if (repository.existsByEmail(user.getEmail())) {
            throw new ResponseStatusException(BAD_REQUEST, "A user with this email already exists");
        }
        var encodedPassword = passwordEncoder.encode(user.getPassword());
        var userEntity = user.toUserEntity(encodedPassword);
        return new UserResponseDto(repository.save(userEntity));
    }

    public UserResponseDto update(Long userId, UserRequestDto user) {
        try {
            getUserOrThrowException(userId);
            var encodedPassword = passwordEncoder.encode(user.getPassword());
            var userEntity = user.toUserEntity(userId, encodedPassword);
            return new UserResponseDto(repository.save(userEntity));
        } catch (Exception ex) {
            throw new ResponseStatusException(BAD_REQUEST, ex.getMessage(), ex);
        }
    }

    private User getUserOrThrowException(Long userId) {
        var user = repository.findById(userId);
        if (user.isEmpty()) throw new ResponseStatusException(NOT_FOUND, "User is not found");
        else return user.get();
    }

    public UserResponseDto delete(Long userId) {
        var user = getUserOrThrowException(userId);
        repository.deleteById(userId);
        return new UserResponseDto(user);
    }

    public User getByUsername(String username) {
        return repository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User is not found"));
    }

    public UserDetailsService userDetailsService() {
        return this::getByUsername;
    }

    public User getCurrentUser() {
        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        return getByUsername(username);
    }
}
