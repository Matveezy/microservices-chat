package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.domain.dto.MessageDeliveryResponseDto;
import org.example.domain.dto.PageableRequest;
import org.example.service.MessageDeliveryService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/message/delivery")
@RolesAllowed("ADMIN")
@RequiredArgsConstructor
@Tag(name = "Message delivery controller")
public class MessageDeliveryController {

    private final MessageDeliveryService messageDeliveryService;

    @Operation(summary = "Find message deliveries")
    @PostMapping("/all")
    public Page<MessageDeliveryResponseDto> getAll(@RequestBody @Valid PageableRequest pageable) {
        return messageDeliveryService.findAll(pageable);
    }

    @Operation(summary = "Create a message delivery")
    @PostMapping("/{messageId}/{receiverId}")
    public MessageDeliveryResponseDto create(
            @PathVariable("messageId") Long messageId,
            @PathVariable("receiverId") Long receiverId
    ) {
        return messageDeliveryService.create(messageId, receiverId);
    }

    @Operation(summary = "Delete a message delivery")
    @DeleteMapping("/{id}")
    public MessageDeliveryResponseDto delete(
            @PathVariable("messageId") Long messageId,
            @PathVariable("receiverId") Long receiverId
    ) {
        return messageDeliveryService.delete(messageId, receiverId);
    }
}

