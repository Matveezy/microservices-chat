package org.example.domain.dto;

import lombok.Data;
import org.example.domain.model.Chat;

import java.util.List;

@Data
public class ChatResponseDto {

    private Long id;

    private String name;

    private Boolean privateB;

    private List<UserResponseDto> users;

    public ChatResponseDto(Chat chat) {
        this.id = chat.getId();
        this.name = chat.getName();
        this.privateB = chat.getPrivateB();
        this.users = chat.getUsers().stream().map(UserResponseDto::new).toList();
    }
}