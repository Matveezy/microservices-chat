package org.example.service;

import org.example.domain.dto.PageableRequest;
import org.example.domain.dto.UserRequestDto;
import org.example.domain.model.Role;
import org.example.domain.model.User;
import org.example.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.example.service.utils.UserServiceUtils.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private final UserRepository userRepository = mock(UserRepository.class);

    private final UserService userService = new UserService(userRepository, passwordEncoder);

    @BeforeEach
    void setUp() {
        when(userRepository.findById(USER_1_ID)).thenReturn(Optional.of(USER_1));
        when(userRepository.findById(USER_2_ID)).thenReturn(Optional.of(USER_1));
        when(userRepository.findById(USER_3_ID)).thenReturn(Optional.empty());
        when(userRepository.save(any())).thenReturn(USER_1);
        var usersResponse = new ArrayList<User>();
        usersResponse.add(USER_1);
        usersResponse.add(USER_2);
        var usersPage = new PageImpl<>(usersResponse, Pageable.unpaged(), usersResponse.size());
        when(userRepository.findAll(any(Pageable.class))).thenReturn(usersPage);
    }

    @Test
    void testFindingAllUsers() {
        // given
        var pageableRequest = new PageableRequest(0, 3);
        // when
        var actualUsers = userService.findAll(pageableRequest);
        // then
        assertThat(actualUsers.getTotalElements()).isEqualTo(2);
    }

    @Test
    void testFindingAllUsersWhenPageableRequestIsNull() {
        // when
        var actualUsers = userService.findAll(null);
        // then
        assertThat(actualUsers.getTotalElements()).isEqualTo(2);
    }

    @Test
    void testFindingAllUsersByInfiniteLoop() {
        // given
        var pageableRequest = new PageableRequest(0, 3);
        // when
        var actualUsers = userService.findAllByInfiniteLoop(pageableRequest);
        // then
        assertThat(actualUsers.size()).isEqualTo(3);
    }

    @Test
    void testFindingAllUsersByInfiniteLoopWhenPageRequiresMoreUsersThanUsersRepositoryContains() {
        // given
        var pageableRequest = new PageableRequest(15, 100);
        // when
        var actualUsers = userService.findAllByInfiniteLoop(pageableRequest);
        // then
        assertThat(actualUsers.size()).isEqualTo(100);
    }

    @Test
    void testFindingAllUsersByInfiniteLoopWhenPageRequiresLessUsersThanUsersRepositoryContains() {
        // given
        var pageableRequest = new PageableRequest(0, 1);
        // when
        var actualUsers = userService.findAllByInfiniteLoop(pageableRequest);
        // then
        assertThat(actualUsers.size()).isEqualTo(1);
    }

    @Test
    void testCreationOfUser() {
        // given
        var userRequestDto = new UserRequestDto("user1", "user1@mail.com", "1234", Role.ROLE_USER.toString());
        // when
        var actualUser = userService.create(userRequestDto);
        // then
        assertThat(actualUser.getId()).isEqualTo(USER_1.getId());
        assertThat(actualUser.getUsername()).isEqualTo(USER_1.getUsername());
        assertThat(actualUser.getEmail()).isEqualTo(USER_1.getEmail());
        assertThat(actualUser.getRole()).isEqualTo(USER_1.getRole().toString());
    }

    @Test
    void testUpdatingUser() {
        // given
        var userRequestDto = new UserRequestDto("user1", "user1@mail.com", "1234", Role.ROLE_USER.toString());
        // when
        var actualUser = userService.update(USER_1_ID, userRequestDto);
        // then
        assertThat(actualUser.getId()).isEqualTo(USER_1.getId());
        assertThat(actualUser.getUsername()).isEqualTo(USER_1.getUsername());
        assertThat(actualUser.getEmail()).isEqualTo(USER_1.getEmail());
        assertThat(actualUser.getRole()).isEqualTo(USER_1.getRole().toString());
    }

    @Test
    void testUpdatingNotExistingUser() {
        // given
        var userRequestDto = new UserRequestDto("user1", "user1@mail.com", "1234", Role.ROLE_USER.toString());
        // when
        var actualException = assertThatThrownBy(() -> userService.update(USER_3_ID, userRequestDto));
        // then
        actualException.hasMessageContaining("User is not found");
    }

    @Test
    void testDeletionOfExistingUser() {
        // when
        var actualUser = userService.delete(USER_1_ID);
        // then
        assertThat(actualUser.getId()).isEqualTo(USER_1.getId());
        assertThat(actualUser.getUsername()).isEqualTo(USER_1.getUsername());
        assertThat(actualUser.getEmail()).isEqualTo(USER_1.getEmail());
        assertThat(actualUser.getRole()).isEqualTo(USER_1.getRole().toString());
    }

    @Test
    void testDeletionOfNotExistingUser() {
        // when
        var actualException = assertThatThrownBy(() -> userService.delete(USER_3_ID));
        // then
        actualException.hasMessageContaining("User is not found");
    }
}