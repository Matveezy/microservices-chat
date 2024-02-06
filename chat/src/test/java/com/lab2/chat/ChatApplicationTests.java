package com.lab2.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lab2.chat.dto.ChatRequestDto;
import com.lab2.chat.dto.ChatResponseDto;
import com.lab2.chat.entity.Chat;
import com.lab2.chat.entity.Role;
import com.lab2.chat.feign.UserServiceClient;
import com.lab2.chat.repository.ChatParticipantRepository;
import com.lab2.chat.repository.ChatRepository;
import com.lab2.chat.security.WithMockCustomUser;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest(classes = ChatApplication.class)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@AutoConfigureMockMvc
@RequiredArgsConstructor
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ChatApplicationTests {

    private static final PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres:latest");

    private final MockMvc mockMvc;
    private final ChatRepository chatRepository;
    private final ChatParticipantRepository chatParticipantRepository;

    @MockBean(classes = {UserServiceClient.class})
    private final UserServiceClient userServiceClient;

    static {
        container.start();
    }

    @Test
    @WithMockCustomUser
    void findChatById_chatExists_success() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/chats/1"))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful()).andReturn();
        Chat chat = chatRepository.findById(1L).block();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        ChatResponseDto chatResponseDto = fromStringToObject(contentAsString, ChatResponseDto.class);
        assertEquals(chatResponseDto.getId(), chat.getId());
        assertEquals(chatResponseDto.getName(), chat.getName());
        assertEquals(chatResponseDto.getIsPrivate(), chat.getIsPrivate());
    }

    @Test
    @WithMockCustomUser
    void findChatById_chatNotExists_receivesBadRequestHttpStatus() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/chats/10"))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError()).andReturn();
    }

    @Test
    @WithMockCustomUser(authority = Role.ROLE_ADMIN)
    void createGroupChat_loggedByAdmin_success() throws Exception {
        ChatRequestDto chatRequestDto = chatRequestDto("New group chat");
        List<Chat> chats = chatRepository.findAll().collectList().block();
        assertEquals(4, chats.size());
        mockMvc.perform(post("/chats/group")
                        .content(asJsonString(chatRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
        List<Chat> chatsAfterCreating = chatRepository.findAll().collectList().block();
        assertEquals(5, chatsAfterCreating.size());
        Chat addedChat = chatRepository.findById(5L).block();
        assertEquals(chatRequestDto.getName(), addedChat.getName());
    }

    @Test
    @WithMockCustomUser(authority = Role.ROLE_USER)
    void createGroupChat_loggedByUser_receivesForbiddenHttpStatus() throws Exception {
        ChatRequestDto chatRequestDto = chatRequestDto("New group chat");
        mockMvc.perform(post("/chats/group")
                        .content(asJsonString(chatRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.FORBIDDEN.value()));
    }

    @Test
    @WithMockCustomUser(authority = Role.ROLE_ADMIN)
    void updateGroupChatName_loggedByAdmin_success() throws Exception {
        ChatRequestDto chatRequestDto = chatRequestDto("Updated group chat name");
        Chat groupChatBeforeUpdating = chatRepository.findById(1L).block();
        assertEquals("First Group Chat", groupChatBeforeUpdating.getName());
        mockMvc.perform(put("/chats/1")
                        .content(asJsonString(chatRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
        Chat groupChatAfterUpdating = chatRepository.findById(1L).block();
        assertEquals(chatRequestDto.getName(), groupChatAfterUpdating.getName());
    }

    @Test
    @WithMockCustomUser(authority = Role.ROLE_USER)
    void updateGroupChatName_loggedByUser_receivesForbiddenHttpStatus()  throws Exception {
        ChatRequestDto chatRequestDto = chatRequestDto("Updated");
        mockMvc.perform(put("/chats/1")
                        .content(asJsonString(chatRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.FORBIDDEN.value()));
    }

    public <T> T fromStringToObject(final String content, Class<T> type) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(content, type);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String asJsonString(final Object obj) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ChatRequestDto chatRequestDto(String chatName) {
        return ChatRequestDto.builder()
                .name(chatName)
                .build();
    }

    @DynamicPropertySource
    static void postgresProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
        dynamicPropertyRegistry.add("spring.datasource.url", container::getJdbcUrl);
        dynamicPropertyRegistry.add("spring.datasource.username", container::getUsername);
        dynamicPropertyRegistry.add("spring.datasource.password", container::getPassword);

        String jdbcUrl = container.getJdbcUrl();
        dynamicPropertyRegistry.add("spring.r2dbc.url", () -> jdbcUrl.replace("jdbc", "r2dbc"));
        dynamicPropertyRegistry.add("spring.r2dbc.username", container::getUsername);
        dynamicPropertyRegistry.add("spring.r2dbc.password", container::getPassword);

        dynamicPropertyRegistry.add("spring.liquibase.url", container::getJdbcUrl);
        dynamicPropertyRegistry.add("spring.liquibase.user", container::getUsername);
        dynamicPropertyRegistry.add("spring.liquibase.password", container::getPassword);

    }

}
