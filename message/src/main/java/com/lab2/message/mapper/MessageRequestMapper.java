package com.lab2.message.mapper;

import com.lab2.message.dto.MessageRequestDto;
import com.lab2.message.entity.Message;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Collections;

@Component
public class MessageRequestMapper implements Mapper<MessageRequestDto, Message>{

    @Override
    public MessageRequestDto mapToDto(Message entity) {
        return null;
    }

    @Override
    public Message mapToEntity(MessageRequestDto dto) {
        return Message.builder()
                .body(dto.getBody())
                .creationTimestamp(Instant.now())
                .messageDeliveries(Collections.emptyList())
                .build();
    }
}
