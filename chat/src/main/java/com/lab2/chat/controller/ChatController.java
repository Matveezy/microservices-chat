package com.lab2.chat.controller;

import com.lab2.chat.dto.ChatRequestDto;
import com.lab2.chat.dto.ChatResponseDto;
import com.lab2.chat.dto.ChatUsersRequestDto;
import com.lab2.chat.service.ChatService;
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

import static com.lab2.chat.util.RequestAttributeNames.*;

@RestController
@RequestMapping("/chats")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @GetMapping
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<ChatResponseDto>> findAll() {
        return new ResponseEntity<>(chatService.findAll().collectList().block(), HttpStatus.OK);
    }

    @GetMapping("/{chatId}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ChatResponseDto> findById(@PathVariable Long chatId) {
        return new ResponseEntity<>(chatService.findById(chatId).block(), HttpStatus.OK);
    }

    @GetMapping("/group")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ChatResponseDto>> findUserGroupChats(HttpServletRequest httpServletRequest) {
        String userId = httpServletRequest.getHeader(USER_ID_ATTRIBUTE_NAME);
        if (userId == null) return ResponseEntity.badRequest().build();
        return new ResponseEntity<>(chatService.findUserGroupChats(Long.valueOf(userId)).collectList().block(), HttpStatus.OK);
    }

    @PostMapping("/group")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<ChatResponseDto> createGroupChat(@RequestBody @Valid ChatRequestDto request) {
        return new ResponseEntity<>(chatService.createGroupChat(request).block(), HttpStatus.OK);
    }

    @GetMapping("/private")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ChatResponseDto>> findUserPrivateChats(HttpServletRequest httpServletRequest) {
        String userId = httpServletRequest.getHeader(USER_ID_ATTRIBUTE_NAME);
        if (userId == null) return ResponseEntity.badRequest().build();
        return new ResponseEntity<>(chatService.findUserPrivateChats(Long.valueOf(userId)).collectList().block(), HttpStatus.OK);

    }

    @GetMapping("/private/{withUserId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ChatResponseDto> findPrivateChat(HttpServletRequest httpServletRequest,
                                                           @PathVariable Long withUserId) {
        String userId = httpServletRequest.getHeader(USER_ID_ATTRIBUTE_NAME);
        if (userId == null) return ResponseEntity.badRequest().build();
        return new ResponseEntity<>(chatService.findPrivateChat(Long.valueOf(userId), withUserId).block(), HttpStatus.OK);
    }

    @PostMapping("/private/{withUserId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ChatResponseDto> createPrivateChat(HttpServletRequest httpServletRequest,
                                                             @PathVariable Long withUserId) {
        String userId = httpServletRequest.getHeader(USER_ID_ATTRIBUTE_NAME);
        if (userId == null) return ResponseEntity.badRequest().build();
        return new ResponseEntity<>(chatService.createPrivateChat(Long.valueOf(userId), withUserId).block(), HttpStatus.OK);
    }

    @PutMapping("/{chatId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<ChatResponseDto> updateGroupChatName(@PathVariable Long chatId,
                                                               @RequestBody @Valid ChatRequestDto request) {
        return new ResponseEntity<>(chatService.updateGroupChatName(chatId, request).block(), HttpStatus.OK);
    }

    @PutMapping("/participants/{chatId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<ChatResponseDto> updateParticipants(@PathVariable Long chatId,
                                                              @RequestBody @Valid ChatUsersRequestDto chatUsersRequest) {
        return new ResponseEntity<>(chatService.updateParticipants(chatId, chatUsersRequest).block(), HttpStatus.OK);
    }

    @DeleteMapping("/{chatId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> deleteChat(@PathVariable Long chatId) {
        return new ResponseEntity<>(chatService.delete(chatId).block(), HttpStatus.OK);
    }

    @ExceptionHandler({FeignException.class})
    public ResponseEntity<?> handleUnexpectedServiceExceptions() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Couldn't make call for external service.");
    }
}
