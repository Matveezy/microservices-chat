package org.example.domain.dto;

import lombok.Data;
import org.example.domain.model.MessageDelivery;

import java.time.Instant;

@Data
public class MessageDeliveryResponseDto {

    private MessageResponseDto message;

    private UserResponseDto receiver;

    private Boolean delivered;

    private Instant deliveryTimestamp;

    public MessageDeliveryResponseDto(MessageDelivery messageDelivery) {
        this.message = new MessageResponseDto(messageDelivery.getMessage());
        this.receiver = new UserResponseDto(messageDelivery.getReceiver());
        this.delivered = messageDelivery.getDelivered();
        this.deliveryTimestamp = messageDelivery.getDeliveryTimestamp();
    }
}