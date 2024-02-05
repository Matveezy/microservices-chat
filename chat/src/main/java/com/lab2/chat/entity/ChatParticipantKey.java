package com.lab2.chat.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Column;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatParticipantKey implements Serializable {

    @Column("user_id")
    private Long userId;

    @Column("chat_id")
    private Long chatId;
}
