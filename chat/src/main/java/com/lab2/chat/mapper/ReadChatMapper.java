package com.lab2.chat.mapper;

import com.lab2.chat.dto.ChatResponseDto;
import com.lab2.chat.entity.Chat;
import org.springframework.stereotype.Component;

@Component
public class ReadChatMapper implements Mapper<ChatResponseDto, Chat> {

    @Override
    public ChatResponseDto mapToDto(Chat entity) {
        return ChatResponseDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .isPrivate(entity.getIsPrivate())
                .userIds(entity.getUserIds())
                .build();
    }

}
