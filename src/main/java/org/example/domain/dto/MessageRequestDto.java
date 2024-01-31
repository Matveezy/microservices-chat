package org.example.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.domain.model.Chat;
import org.example.domain.model.Message;
import org.example.domain.model.User;

import java.time.Instant;
import java.util.Collections;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Message request")
public class MessageRequestDto {

    @Schema(description = "Body", example = "Hi!")
    @Size(min = 1, max = 255, message = "Body must contain from 1 to 255 characters")
    @NotBlank(message = "Body cannot be empty")
    private String body;

    public MessageRequestDto(Message message) {
        this.body = message.getBody();
    }

    public Message toMessageEntity(User sender, Chat chat) {
        return this.toMessageEntity(null, sender, chat);
    }

    public Message toMessageEntity(Long id, User sender, Chat chat) {
        return new Message(
                id,
                this.body,
                Instant.now(),
                sender,
                chat,
                Collections.emptyList());
    }
}