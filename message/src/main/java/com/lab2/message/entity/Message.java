package com.lab2.message.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.List;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "messages")
public class Message {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "body", nullable = false)
    private String body;

    @Column(name = "creation_ts", nullable = false)
    private Instant creationTimestamp;

    @Column(name = "sender_id", nullable = false)
    private Long senderId;

    @Column(name = "chat_id", nullable = false)
    private Long chatId;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "message")
    private List<MessageDelivery> messageDeliveries;
}
