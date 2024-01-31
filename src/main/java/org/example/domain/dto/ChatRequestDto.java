package org.example.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.domain.model.Chat;
import org.example.domain.model.User;

import java.util.Collections;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Chat request")
public class ChatRequestDto {

    @Schema(description = "Name", example = "SuperChat")
    @Size(min = 4, max = 50, message = "Name must contain from 4 to 50 characters")
    @NotBlank(message = "Name cannot be empty")
    private String name;

    public ChatRequestDto(Chat chat) {
        this.name = chat.getName();
    }

    public Chat toChatEntity() {
        return this.toChatEntity(null, false, Collections.emptyList());
    }

    public Chat toChatEntity(Boolean isPrivate, List<User> participants) {
        return this.toChatEntity(null, isPrivate, participants);
    }

    public Chat toChatEntity(Long id, Boolean isPrivate, List<User> participants) {
        return new Chat(
                id,
                this.name,
                isPrivate,
                participants);
    }
}