package com.lab2.message.controller;

import com.lab2.message.dto.MessageRequestDto;
import com.lab2.message.dto.MessageResponseDto;
import com.lab2.message.dto.MessageResponseForReadingDto;
import com.lab2.message.service.MessageService;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.lab2.message.util.RequestAttributeUtils.*;

@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<MessageResponseDto>> getAll() {
        return new ResponseEntity<>(messageService.findAll(), HttpStatus.OK);
    }

    @PostMapping("/chats/{chatId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<MessageResponseDto> create(HttpServletRequest httpServletRequest,
                                                     @PathVariable Long chatId,
                                                     @RequestBody @Valid MessageRequestDto messageRequestDto) {
        Long userId = extractUserId(httpServletRequest);
        return new ResponseEntity<>(messageService.create(userId, chatId, messageRequestDto), HttpStatus.OK);
    }

    @PostMapping("/chats/{chatId}/send")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MessageResponseDto> sendMessage(HttpServletRequest httpServletRequest,
                                                          @PathVariable Long chatId,
                                                          @RequestBody @Valid MessageRequestDto messageRequestDto) {
        Long userId = extractUserId(httpServletRequest);
        return new ResponseEntity<>(messageService.sendMessage(userId, chatId, messageRequestDto), HttpStatus.OK);
    }

    @GetMapping("/chats/{chatId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<MessageResponseForReadingDto>> readMessagesInChat(HttpServletRequest httpServletRequest,
                                                                          @PathVariable Long chatId) {
        Long userId = extractUserId(httpServletRequest);
        return new ResponseEntity<>(messageService.readMessage(userId, chatId), HttpStatus.OK);
    }

    @PutMapping("/{messageId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MessageResponseDto> updateMessage(HttpServletRequest httpServletRequest,
                                                            @PathVariable Long messageId,
                                                            @RequestBody @Valid MessageRequestDto messageRequestDto) {
        Long userId = extractUserId(httpServletRequest);
        return new ResponseEntity<>(messageService.update(userId, messageId, messageRequestDto), HttpStatus.OK);
    }

    @DeleteMapping("/{messageId}")
    public ResponseEntity<?> deleteMessage(HttpServletRequest httpServletRequest,
                                           @PathVariable Long messageId) {
        Long userId = extractUserId(httpServletRequest);
        messageService.deleteMessage(userId, messageId);
        return ResponseEntity.ok().build();
    }

    @ExceptionHandler({FeignException.class})
    public ResponseEntity<?> handleUnexpectedServiceExceptions() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Couldn't make call for external service.");
    }

}
