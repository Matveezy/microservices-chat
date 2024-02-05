package com.lab2.message.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageDeliveryForReadingResponseDto {

    private Long userId;

    private Boolean delivered;

    private Instant deliveryTimestamp;

}