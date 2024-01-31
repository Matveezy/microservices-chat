package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import org.example.domain.dto.MessageRequestDto;
import org.example.domain.dto.MessageResponseDto;
import org.example.domain.dto.MessageResponseForReadingDto;
import org.example.domain.dto.PageableRequest;
import org.example.service.MessageService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/message")
@RequiredArgsConstructor
@Tag(name = "Message controller")
public class MessageController {

    private final MessageService messageService;

    @RolesAllowed("ADMIN")
    @Operation(summary = "Find all messages")
    @PostMapping("/all")
    public Page<MessageResponseDto> getAll(
            @RequestBody @Valid PageableRequest pageable
    ) {
        return messageService.findAll(pageable);
    }


    @RolesAllowed("ADMIN")
    @Operation(summary = "Create a message")
    @PostMapping("/{chatId}")
    public MessageResponseDto create(@PathParam("chatId") Long chatId, @RequestBody @Valid MessageRequestDto request) {
        return messageService.create(chatId, request);
    }

    @Operation(summary = "Send a message")
    @PostMapping("/{chatId}/send")
    public MessageResponseDto sendMessage(@PathParam("chatId") Long chatId, @RequestBody @Valid MessageRequestDto request) {
        return messageService.sendMessage(chatId, request);
    }

    @Operation(summary = "Read messages")
    @PostMapping("/{chatId}/read")
    public Page<MessageResponseForReadingDto> readMessages(
            @PathParam("chatId") Long chatId,
            @RequestBody @Valid PageableRequest pageable
    ) {
        return messageService.readMessages(chatId, pageable);
    }

    @RolesAllowed("ADMIN")
    @Operation(summary = "Update a message")
    @PutMapping("/{id}/{chatId}")
    public MessageResponseDto update(
            @PathParam("id") Long id,
            @PathParam("chatId") Long chatId,
            @RequestBody @Valid MessageRequestDto request
    ) {
        return messageService.update(id, chatId, request);
    }


    @RolesAllowed("ADMIN")
    @Operation(summary = "Delete a message")
    @DeleteMapping("/{id}")
    public MessageResponseDto delete(@PathVariable("id") Long id) {
        return messageService.delete(id);
    }
}

