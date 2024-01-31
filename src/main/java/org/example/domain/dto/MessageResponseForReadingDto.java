package org.example.domain.dto;

import lombok.Data;
import org.example.domain.model.Message;
import org.example.domain.model.MessageDelivery;

import java.time.Instant;
import java.util.List;

@Data
public class MessageResponseForReadingDto {

    private String body;

    private Instant creationTimestamp;

    private String senderName;

    private List<MessageDeliveryForReadingResponseDto> messageDeliveries;

    public MessageResponseForReadingDto(Message message) {
        this.body = message.getBody();
        this.creationTimestamp = message.getCreationTimestamp();
        this.senderName = message.getSender().getUsername();
    }

    public MessageResponseForReadingDto(Message message, List<MessageDelivery> messageDeliveries) {
        this.body = message.getBody();
        this.creationTimestamp = message.getCreationTimestamp();
        this.senderName = message.getSender().getUsername();
        this.messageDeliveries = messageDeliveries.stream().map(MessageDeliveryForReadingResponseDto::new).toList();
    }
}