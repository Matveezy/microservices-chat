package com.lab2.message.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "message_deliveries")
public class MessageDelivery {

    @EmbeddedId
    private MessageDeliveryKey messageDeliveryKey;

    @ManyToOne
    @MapsId("messageId")
    @JoinColumn(name = "message_id")
    private Message message;

    @Column(name = "delivered", nullable = false)
    private Boolean delivered;

    @Column(name = "delivery_ts")
    private Instant deliveryTimestamp;
}
