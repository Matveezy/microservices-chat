package org.example.domain.dto;

import lombok.Data;
import org.example.domain.model.MessageDelivery;

import java.time.Instant;

@Data
public class MessageDeliveryForReadingResponseDto {

    private String receiverName;

    private Boolean delivered;

    private Instant deliveryTimestamp;

    public MessageDeliveryForReadingResponseDto(MessageDelivery messageDelivery) {
        this.receiverName = messageDelivery.getReceiver().getUsername();
        this.delivered = messageDelivery.getDelivered();
        this.deliveryTimestamp = messageDelivery.getDeliveryTimestamp();
    }
}