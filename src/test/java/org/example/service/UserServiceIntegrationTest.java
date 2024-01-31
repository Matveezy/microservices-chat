package org.example.service;

import org.example.domain.dto.PageableRequest;
import org.example.domain.dto.UserRequestDto;
import org.example.domain.model.Role;
import org.example.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.example.service.utils.UserServiceUtils.*;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserServiceIntegrationTest {

    private final Long NOT_EXISTING_ID = 9999L;
    private final UserRequestDto USER_REQUEST_DTO = new UserRequestDto(
            USER_3.getUsername(),
            USER_3.getEmail(),
            USER_3.getPassword(),
            USER_3.getRole().toString());

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private UserRepository userRepository;

    private UserService userService;

    @BeforeAll
    static void beforeAll() {
        postgres.start();
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

    @BeforeEach
    void setUp() {
        userRepository.save(USER_1);
        userRepository.save(USER_2);
        if (userService == null)
            userService = new UserService(userRepository, passwordEncoder);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
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
        // when
        var actualUser = userService.create(USER_REQUEST_DTO);
        // then
        assertThat(actualUser.getUsername()).isEqualTo(USER_3.getUsername());
        assertThat(actualUser.getEmail()).isEqualTo(USER_3.getEmail());
        assertThat(actualUser.getRole()).isEqualTo(Role.ROLE_USER.toString());
    }

    @Test
    void testUpdatingUser() {
        // given
        var userToUpdate = userRepository.save(USER_3);
        // when
        var actualUser = userService.update(userToUpdate.getId(), USER_REQUEST_DTO);
        // then
        assertThat(actualUser.getId()).isEqualTo(userToUpdate.getId());
        assertThat(actualUser.getUsername()).isEqualTo(userToUpdate.getUsername());
        assertThat(actualUser.getEmail()).isEqualTo(userToUpdate.getEmail());
        assertThat(actualUser.getRole()).isEqualTo(userToUpdate.getRole().toString());
    }

    @Test
    void testUpdatingNotExistingUser() {
        // when
        var actualException = assertThatThrownBy(() -> userService.update(NOT_EXISTING_ID, USER_REQUEST_DTO));
        // then
        actualException.hasMessageContaining("User is not found");
    }

    @Test
    void testDeletionOfExistingUser() {
        // given
        var userToDelete = userRepository.save(USER_3);
        // when
        var actualUser = userService.delete(userToDelete.getId());
        // then
        assertThat(actualUser.getId()).isEqualTo(userToDelete.getId());
        assertThat(actualUser.getUsername()).isEqualTo(userToDelete.getUsername());
        assertThat(actualUser.getEmail()).isEqualTo(userToDelete.getEmail());
        assertThat(actualUser.getRole()).isEqualTo(userToDelete.getRole().toString());
    }

    @Test
    void testDeletionOfNotExistingUser() {
        // when
        var actualException = assertThatThrownBy(() -> userService.delete(NOT_EXISTING_ID));
        // then
        actualException.hasMessageContaining("User is not found");
    }
}
