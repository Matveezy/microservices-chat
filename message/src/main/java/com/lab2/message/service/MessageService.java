package com.lab2.message.service;

import com.lab2.message.dto.ChatResponseDto;
import com.lab2.message.dto.MessageRequestDto;
import com.lab2.message.dto.MessageResponseDto;
import com.lab2.message.dto.MessageResponseForReadingDto;
import com.lab2.message.entity.Message;
import com.lab2.message.entity.MessageDelivery;
import com.lab2.message.entity.MessageDeliveryKey;
import com.lab2.message.exception.ChatNotFoundException;
import com.lab2.message.exception.ChatPermissionException;
import com.lab2.message.exception.MessagePermissionException;
import com.lab2.message.feign.ChatServiceClient;
import com.lab2.message.mapper.MessageDeliveryReadMapper;
import com.lab2.message.mapper.MessageRequestMapper;
import com.lab2.message.mapper.MessageResponseMapper;
import com.lab2.message.repository.MessageDeliveryRepository;
import com.lab2.message.repository.MessageRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MessageService {

    private final MessageRepository messageRepository;
    private final MessageResponseMapper messageResponseMapper;
    private final ChatServiceClient chatServiceClient;
    private final MessageRequestMapper messageRequestMapper;
    private final MessageDeliveryService messageDeliveryService;
    private final MessageDeliveryReadMapper messageDeliveryReadMapper;

    public List<MessageResponseDto> findAll() {
        return messageRepository.findAll().stream()
                .map(messageResponseMapper::mapToDto)
                .toList();
    }

    @Transactional
    public MessageResponseDto create(Long userId, Long chatId, MessageRequestDto messageRequestDto) {
        ChatResponseDto chat = getChat(chatId);
        if (!isChatAllowedForUser(chat, userId))
            throw new ChatPermissionException("The user can't send/read messages in the chat " + chat.getName());
        Message messageEntityToSave = messageRequestMapper.mapToEntity(messageRequestDto);
        messageEntityToSave.setSenderId(userId);
        messageEntityToSave.setChatId(chatId);
        return messageResponseMapper.mapToDto(messageRepository.save(messageEntityToSave));
    }

    @Transactional
    public MessageResponseDto sendMessage(Long userId, Long chatId, MessageRequestDto messageRequestDto) {
        ChatResponseDto chat = getChat(chatId);
        Message messageEntity = messageRequestMapper.mapToEntity(messageRequestDto);
        messageEntity.setSenderId(userId);
        messageEntity.setChatId(chatId);
        messageRepository.save(messageEntity);
        List<Long> receiversIds = getReceivers(chat, userId);
        for (Long receiverId : receiversIds) {
            messageDeliveryService.create(receiverId, messageEntity);
        }
        return messageResponseMapper.mapToDto(messageEntity);
    }

    public List<MessageResponseForReadingDto> readMessage(Long userId, Long chatId) {
        ChatResponseDto chat = getChat(chatId);
        // get all messages in chat
        List<Message> allMessagesInChat = messageRepository.findAllByChatId(chatId);
        // make messages deliverired by us
        messageDeliveryService.updateMessageDeliveriesIfNecessary(allMessagesInChat, userId);
        // get chat participants
        List<Long> chatUsersIds = chat.getUserIds();
        return allMessagesInChat.stream().map(message -> {
            List<MessageDelivery> messageDeliveries = chatUsersIds.stream()
                    .filter(id -> !id.equals(userId))
                    .map(participantId -> {
                return messageDeliveryService.findByKey(MessageDeliveryKey.builder().messageId(message.getId()).receiverId(participantId).build())
                        .orElse(null);
            }).filter(Objects::nonNull).toList();
            return MessageResponseForReadingDto.builder().body(message.getBody())
                    .messageDeliveries(messageDeliveries.stream().map(messageDeliveryReadMapper::mapToDto).toList())
                    .creationTimestamp(message.getCreationTimestamp())
                    .senderId(message.getSenderId())
                    .build();
        }).toList();
    }

    @Transactional
    public MessageResponseDto update(Long userId, Long messageId, MessageRequestDto messageRequestDto) {
        Message message = messageRepository.findById(messageId).orElseThrow(EntityNotFoundException::new);
        if (!Objects.equals(message.getSenderId(), userId))
            throw new MessagePermissionException("You can't update this message!");
        message.setBody(messageRequestDto.getBody());
        return messageResponseMapper.mapToDto(messageRepository.save(message));
    }

    @Transactional
    public void deleteMessage(Long userId, Long messageId) {
        Message message = messageRepository.findById(messageId).orElseThrow(EntityNotFoundException::new);
        if (!Objects.equals(message.getSenderId(), userId))
            throw new MessagePermissionException("You can't update this message!");
        messageRepository.deleteById(messageId);
    }

    private List<Long> getReceivers(ChatResponseDto chat, Long currentUserId) {
        return chat.getUserIds().stream().filter(userId -> !userId.equals(currentUserId)).toList();
    }

    private boolean isChatAllowedForUser(ChatResponseDto chat, Long userId) {
        return chat.getUserIds().stream().anyMatch(id -> Objects.equals(id, userId));
    }

    private ChatResponseDto getChat(Long chatId) {
        ResponseEntity<Optional<ChatResponseDto>> chatByIdOptional = chatServiceClient.findById(chatId);
        if (!chatByIdOptional.hasBody())
            throw new ChatNotFoundException("Chat with id " + chatId + " doesn't exist!");
        return chatByIdOptional.getBody().get();
    }

}
