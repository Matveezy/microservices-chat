package com.lab2.message.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageResponseForReadingDto {

    private String body;

    private Instant creationTimestamp;

    private Long senderId;

    private List<MessageDeliveryForReadingResponseDto> messageDeliveries;

}