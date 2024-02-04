package com.lab2.chat.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatRequestDto {

    @Size(min = 4, max = 50, message = "Name must contain from 4 to 50 characters")
    @NotBlank(message = "Name cannot be empty")
    private String name;

}