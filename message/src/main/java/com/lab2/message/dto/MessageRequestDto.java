package com.lab2.message.dto;

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
public class MessageRequestDto {

    @Size(min = 1, max = 255, message = "Body must contain from 1 to 255 characters")
    @NotBlank(message = "Body cannot be empty")
    private String body;

}