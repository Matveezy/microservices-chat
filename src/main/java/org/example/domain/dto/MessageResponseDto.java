package org.example.domain.dto;

import lombok.Data;
import org.example.domain.model.Message;

import java.time.Instant;

@Data
public class MessageResponseDto {

    private Long id;

    private String body;

    private Instant creationTimestamp;

    private UserResponseDto sender;

    private ChatResponseDto chat;

    public MessageResponseDto(Message message) {
        this.id = message.getId();
        this.body = message.getBody();
        this.creationTimestamp = message.getCreationTimestamp();
        this.sender = new UserResponseDto(message.getSender());
        this.chat = new ChatResponseDto(message.getChat());
    }
}