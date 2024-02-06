package com.lab2.message;

import com.lab2.message.dto.ChatResponseDto;
import com.lab2.message.dto.MessageRequestDto;
import com.lab2.message.entity.Message;
import com.lab2.message.entity.MessageDelivery;
import com.lab2.message.feign.ChatServiceClient;
import com.lab2.message.repository.MessageDeliveryRepository;
import com.lab2.message.repository.MessageRepository;
import com.lab2.message.security.WithMockCustomUser;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = MessageApplication.class)
@Transactional
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@AutoConfigureMockMvc
@RequiredArgsConstructor
@Sql(value = "classpath:sql/init.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class MessageApplicationTests {

    private static final PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres:latest");

    static {
        container.start();
    }

    private final MessageRepository messageRepository;
    private final MessageDeliveryRepository messageDeliveryRepository;
    private final MockMvc mockMvc;

    @MockBean(classes = {ChatServiceClient.class})
    private final ChatServiceClient chatServiceClient;

    @Test
    @WithMockCustomUser
    void sendMessage_success() throws Exception {
        List<Long> receivers = List.of(2L, 3L);
        mockChatServiceFeignClient(1L, "First Chat", false, receivers);
        List<MessageDelivery> messageDeliveriesBefore = messageDeliveryRepository.findAll();
        List<Message> messagesBefore = messageRepository.findAll();
        mockMvc.perform(post("/messages/chats/1/send")
                .header("userId", "1")
                .content(asJsonString(messageRequestDto()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().is2xxSuccessful());
        List<Message> messagesAfter = messageRepository.findAll();
        assertEquals(messagesAfter.size(), messagesBefore.size() + 1);
        Message sendedMessage = messagesAfter.get(messagesAfter.size() - 1);
        assertEquals(messageRequestDto().getBody(), sendedMessage.getBody());
        List<MessageDelivery> messageDeliveriesAfter = messageDeliveryRepository.findAll();
        assertEquals(messageDeliveriesAfter.size(), messageDeliveriesBefore.size() + receivers.size());
    }

    @Test
    @WithMockCustomUser
    void sendMessage_chatNotExists_receivesBadHttpStatus() throws Exception {
        mockChatNotExists();
        mockMvc.perform(post("/messages/chats/1/send")
                .header("userId", "1")
                .content(asJsonString(messageRequestDto()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockCustomUser
    void readMessages_success() throws Exception {
        List<Long> participants = List.of(1L, 2L, 3L);
        mockChatServiceFeignClient(1L, "First Chat", false, participants);
        List<Message> allByChatIdBeforeReading = messageRepository.findAllByChatId(1L);
        for (Message msg : allByChatIdBeforeReading) {
            List<MessageDelivery> messageDeliveriesBeforeReading = messageDeliveryRepository.findAll().stream()
                    .filter(messageDelivery ->
                            messageDelivery.getMessageDeliveryKey().getMessageId().equals(msg.getId())
                            && !messageDelivery.getMessage().getSenderId().equals(1L)
                            && messageDelivery.getMessageDeliveryKey().getReceiverId().equals(1L)
                    ).toList();
            for (MessageDelivery delivery : messageDeliveriesBeforeReading) {
                assertFalse(delivery.getDelivered());
            }
        }

        mockMvc.perform(get("/messages/chats/1")
                .header("userId", "1")
        ).andExpect(status().is2xxSuccessful());

        List<Message> allByChatIdAfterReading = messageRepository.findAllByChatId(1L);
        for (Message msg : allByChatIdAfterReading) {
            List<MessageDelivery> messageDeliveriesAfterReading = messageDeliveryRepository.findAll().stream()
                    .filter(messageDelivery ->
                            messageDelivery.getMessageDeliveryKey().getMessageId().equals(msg.getId())
                            && !messageDelivery.getMessage().getSenderId().equals(1L)
                            && messageDelivery.getMessageDeliveryKey().getReceiverId().equals(1L)
                    ).toList();
            for (MessageDelivery delivery : messageDeliveriesAfterReading) {
                assertTrue(delivery.getDelivered());
            }
        }
    }

    @Test
    @WithMockCustomUser
    void readMessages_chatNotExists_receivesBadHttpStatus() throws Exception {
        mockChatNotExists();
        mockMvc.perform(get("/messages/chats/1")
                .header("userId", "1")
        ).andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockCustomUser
    void updateMessage_success() throws Exception {
        MessageRequestDto messageRequestDto = messageRequestDto();
        mockMvc.perform(put("/messages/1")
                .header("userId", "1")
                .content(asJsonString(messageRequestDto()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().is2xxSuccessful());
        Message messageAfterUpdating = messageRepository.findById(1L).get();
        assertEquals(messageRequestDto.getBody(), messageAfterUpdating.getBody());
    }

    @Test
    @WithMockCustomUser
    void updateMessage_messageByNotLoggedUser_receivesForbiddenHttpStatus() throws Exception {
        MessageRequestDto messageRequestDto = messageRequestDto();
        mockMvc.perform(put("/messages/2")
                .header("userId", "1")
                .content(asJsonString(messageRequestDto()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().is(HttpStatus.FORBIDDEN.value()));
    }

    public void mockChatServiceFeignClient(Long chatId, String chatName, Boolean isPrivate, List<Long> userIds) {
        Mockito.when(chatServiceClient.findById(chatId))
                .thenReturn(ResponseEntity.ok(Optional.of(
                        ChatResponseDto.builder()
                                .id(chatId)
                                .name(chatName)
                                .isPrivate(isPrivate)
                                .userIds(userIds)
                                .build()
                )));
    }

    public void mockChatNotExists() {
        Mockito.when(chatServiceClient.findById(any()))
                .thenReturn(ResponseEntity.of(Optional.empty()));
    }

    public MessageRequestDto messageRequestDto() {
        return MessageRequestDto.builder()
                .body("hello")
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

        dynamicPropertyRegistry.add("spring.liquibase.url", container::getJdbcUrl);
        dynamicPropertyRegistry.add("spring.liquibase.user", container::getUsername);
        dynamicPropertyRegistry.add("spring.liquibase.password", container::getPassword);
    }
}
