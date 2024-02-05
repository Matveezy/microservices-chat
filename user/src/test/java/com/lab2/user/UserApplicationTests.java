package com.lab2.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lab2.user.dto.CreateUserDto;
import com.lab2.user.dto.UpdateUserDto;
import com.lab2.user.entity.Role;
import com.lab2.user.entity.User;
import com.lab2.user.repository.UserRepository;
import com.lab2.user.security.WithMockCustomUser;
import com.lab2.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest(classes = UserApplication.class)
@Transactional
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@AutoConfigureMockMvc
@RequiredArgsConstructor
@Sql(value = "classpath:sql/init.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class UserApplicationTests {

    private final UserRepository userRepository;
    private final MockMvc mockMvc;
    private final UserService userService;

    private static final PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres:latest");

    static {
        container.start();
    }

    @Test
    @WithAnonymousUser
    void createUser_success() throws Exception {
        List<User> allUsers = userRepository.findAll();
        allUsers.forEach(System.out::println);
        assertEquals(2, allUsers.size());
        mockMvc.perform(post("/users/register")
                        .content(asJsonString(createUserDto()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
        List<User> allUsersAfterRegister = userRepository.findAll();
        assertEquals(3, allUsersAfterRegister.size());
    }

    @Test
    @WithMockCustomUser()
    void findUserById_userNotExist_receivesBadHttpStatus() throws Exception {
        List<User> allUsers = userRepository.findAll();
        allUsers.forEach(System.out::println);
        assertEquals(2, allUsers.size());
        MvcResult mvcResult = mockMvc.perform(get("/users/" + "4"))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andReturn();
        assertTrue(mvcResult.getResponse().getContentAsString().contains("doesn't exist"));
    }

    @Test
    @WithMockCustomUser
    void updateUser_userNotExist_receivesBadHttpStatus() throws Exception {
        List<User> all = userRepository.findAll();
        all.forEach(System.out::println);
        MvcResult mvcResult = mockMvc.perform(put("/users")
                        .content(asJsonString(createUserDto()))
                        .requestAttr("userId", "4")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError()).andReturn();
        assertTrue(mvcResult.getResponse().getContentAsString().contains("doesn't exist"));
    }

    @Test
    @WithMockCustomUser(authority = Role.ROLE_ADMIN)
    void delete_userNotExist_receivesBadHttpStatus() throws Exception {
        List<User> all = userRepository.findAll();
        all.forEach(System.out::println);
        MvcResult mvcResult = mockMvc.perform(delete("/users/" + "4"))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError()).andReturn();
        assertTrue(mvcResult.getResponse().getContentAsString().contains("doesn't exist"));
    }

    @Test
    @WithMockCustomUser(authority = Role.ROLE_USER)
    void delete_loggedByUser_receivesForbiddenHttpStatus() throws Exception {
        List<User> all = userRepository.findAll();
        all.forEach(System.out::println);
        UpdateUserDto updateUserDto = updateUserDto();
        mockMvc.perform(delete("/users/" + "4"))
                .andExpect(MockMvcResultMatchers.status().is(403)).andReturn();
    }

    public CreateUserDto createUserDto() {
        return CreateUserDto.builder()
                .username("test-user")
                .email("test-user@mail.ru")
                .password("pass")
                .role("ROLE_ADMIN")
                .build();
    }

    public UpdateUserDto updateUserDto() {
        return UpdateUserDto.builder()
                .username("updated username")
                .email("updated@mail.ru")
                .build();
    }

    public String asJsonString(final Object obj) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @DynamicPropertySource
    static void postgresProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
        dynamicPropertyRegistry.add("spring.datasource.url", container::getJdbcUrl);
        dynamicPropertyRegistry.add("spring.datasource.username", container::getUsername);
        dynamicPropertyRegistry.add("spring.datasource.password", container::getPassword);
    }
}
