package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import org.example.domain.dto.ChatRequestDto;
import org.example.domain.dto.ChatUsersRequestDto;
import org.example.domain.dto.ChatResponseDto;
import org.example.domain.dto.PageableRequest;
import org.example.service.ChatService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
@Tag(name = "Chat controller")
public class ChatController {

    private final ChatService chatService;

    @RolesAllowed("ADMIN")
    @Operation(summary = "Find all chats")
    @PostMapping("/all")
    public Page<ChatResponseDto> getAll(@RequestBody @Valid PageableRequest pageable) {
        return chatService.findAll(pageable);
    }

    @Operation(summary = "Find my private chats")
    @PostMapping("/private/my")
    public Page<ChatResponseDto> findMyPrivateChats(@RequestBody @Valid PageableRequest pageable) {
        return chatService.findMyPrivateChats(pageable);
    }

    @Operation(summary = "Find my group chats")
    @PostMapping("/group/my")
    public Page<ChatResponseDto> findMyGroupChats(@RequestBody @Valid PageableRequest pageable) {
        return chatService.findMyGroupChats(pageable);
    }

    @Operation(summary = "Find a private chat")
    @GetMapping("/private/my/{userId}")
    public ChatResponseDto findMyPrivateChat(@PathParam("userId") Long userId) {
        return chatService.findMyPrivateChat(userId);
    }

    @RolesAllowed("ADMIN")
    @Operation(summary = "Create an empty group chat")
    @PostMapping
    public ChatResponseDto createGroupChat(@RequestBody @Valid ChatRequestDto request) {
        return chatService.createGroupChat(request);
    }

    @Operation(summary = "Create a private chat")
    @PostMapping("/private/{userId}")
    public ChatResponseDto createPrivateChat(@PathParam("userId") Long userId) {
        return chatService.createPrivateChat(userId);
    }

    @RolesAllowed("ADMIN")
    @Operation(summary = "Update a chat name")
    @PutMapping("/{id}")
    public ChatResponseDto update(@PathParam("id") Long id, @RequestBody @Valid ChatRequestDto request) {
        return chatService.update(id, request);
    }

    @RolesAllowed("ADMIN")
    @Operation(summary = "Add users to a chat")
    @PutMapping("/{id}/update/users")
    public ChatResponseDto updateUsers(
            @PathParam("id") Long id,
            @RequestBody @Valid ChatUsersRequestDto chatUsersRequest) {
        return chatService.updateParticipants(id, chatUsersRequest);
    }

    @RolesAllowed("ADMIN")
    @Operation(summary = "Delete a chat")
    @DeleteMapping("/{id}")
    public ChatResponseDto delete(@PathVariable("id") Long id) {
        return chatService.delete(id);
    }
}

