package com.lab2.chat.mapper;

import com.lab2.chat.dto.ChatRequestDto;
import com.lab2.chat.entity.Chat;
import org.springframework.stereotype.Component;

@Component
public class CreateChatMapper implements Mapper<ChatRequestDto, Chat>{

    @Override
    public ChatRequestDto mapToDto(Chat entity) {
        return null;
    }

    @Override
    public Chat mapToEntity(ChatRequestDto dto) {
        return Chat.builder()
                .name(dto.getName())
                .build();
    }
}
