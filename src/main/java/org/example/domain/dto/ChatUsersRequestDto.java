package org.example.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Chat users request")
public class ChatUsersRequestDto {

    @Schema(description = "Users", example = "[ 1, 2 ]")
    private List<Long> usersIds;
}
