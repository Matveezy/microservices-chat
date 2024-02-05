package com.lab2.chat.mapper;

import com.lab2.chat.dto.ChatResponseDto;
import com.lab2.chat.entity.Chat;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReadChatMapper implements Mapper<ChatResponseDto, Chat> {

    @Override
    public ChatResponseDto mapToDto(Chat entity) {
        ChatResponseDto chatResponseDto = ChatResponseDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .isPrivate(entity.getIsPrivate())
                .build();
        return chatResponseDto;
    }
}
