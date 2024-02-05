package com.lab2.message.mapper;

import com.lab2.message.dto.MessageResponseDto;
import com.lab2.message.entity.Message;
import org.springframework.stereotype.Component;

@Component
public class MessageResponseMapper implements Mapper<MessageResponseDto, Message>{

    @Override
    public MessageResponseDto mapToDto(Message entity) {
        return MessageResponseDto.builder()
                .id(entity.getId())
                .senderId(entity.getSenderId())
                .chatId(entity.getChatId())
                .creationTimestamp(entity.getCreationTimestamp())
                .body(entity.getBody())
                .build();
    }

}
