package com.lab2.message.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Builder
@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MessageDeliveryKey implements Serializable {

    @Column(name = "message_id")
    private Long messageId;

    @Column(name = "receiver_id")
    private Long receiverId;
}
