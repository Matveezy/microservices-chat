package com.lab2.message.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatResponseDto {

    private Long id;

    private String name;

    private Boolean isPrivate;

    private List<Long> userIds;
}