package com.lab2.chat.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "chat_participants")
public class ChatParticipant {

    @Id
    @Column("user_id")
    private Long userId;

    @Column("chat_id")
    private Long chatId;
}
