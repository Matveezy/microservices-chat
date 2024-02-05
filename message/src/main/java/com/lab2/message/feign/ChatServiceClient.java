package com.lab2.message.feign;

import com.lab2.message.dto.ChatResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@FeignClient(name = "chat")
public interface ChatServiceClient {

    @GetMapping("/chats/{chatId}")
    ResponseEntity<Optional<ChatResponseDto>> findById(@PathVariable Long chatId);
}
