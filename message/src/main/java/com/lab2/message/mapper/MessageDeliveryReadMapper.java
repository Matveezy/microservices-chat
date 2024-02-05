package com.lab2.message.mapper;

import com.lab2.message.dto.MessageDeliveryForReadingResponseDto;
import com.lab2.message.entity.MessageDelivery;
import org.springframework.stereotype.Component;

@Component
public class MessageDeliveryReadMapper implements Mapper<MessageDeliveryForReadingResponseDto, MessageDelivery>{

    @Override
    public MessageDeliveryForReadingResponseDto mapToDto(MessageDelivery entity) {
        return MessageDeliveryForReadingResponseDto.builder()
                .userId(entity.getMessageDeliveryKey().getReceiverId())
                .deliveryTimestamp(entity.getDeliveryTimestamp())
                .delivered(entity.getDelivered())
                .build();
    }
}
