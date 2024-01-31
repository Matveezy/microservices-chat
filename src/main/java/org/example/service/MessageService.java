package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.domain.dto.MessageRequestDto;
import org.example.domain.dto.MessageResponseDto;
import org.example.domain.dto.MessageResponseForReadingDto;
import org.example.domain.dto.PageableRequest;
import org.example.domain.model.*;
import org.example.repository.ChatRepository;
import org.example.repository.MessageDeliveryRepository;
import org.example.repository.MessageRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

import static org.example.domain.dto.PageableRequest.getPageable;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;

    private final MessageDeliveryRepository messageDeliveryRepository;

    private final UserService userService;

    private final ChatRepository chatRepository;

    public Page<MessageResponseDto> findAll(PageableRequest pageableRequest) {
        var pageable = getPageable(pageableRequest, "id");
        var messages = messageRepository.findAll(pageable);
        var messagesResponse = messages.stream().map(MessageResponseDto::new).toList();
        return new PageImpl<>(messagesResponse, pageable, messages.getTotalElements());
    }

    public MessageResponseDto create(Long chatId, MessageRequestDto message) {
        var currentUser = userService.getCurrentUser();
        var chat = getChatOrThrowException(chatId, currentUser.getId());
        var messageEntity = message.toMessageEntity(currentUser, chat);
        return new MessageResponseDto(messageRepository.save(messageEntity));
    }

    @Transactional
    public MessageResponseDto sendMessage(Long chatId, MessageRequestDto message) {
        var currentUser = userService.getCurrentUser();
        var chat = getChatOrThrowException(chatId, currentUser.getId());
        var messageEntity = message.toMessageEntity(currentUser, chat);
        var savedMessage = messageRepository.save(messageEntity);
        var receivers = getReceivers(chat, currentUser);
        for (User receiver : receivers) {
            var key = new MessageDeliveryKey(savedMessage.getId(), receiver.getId());
            var messageDelivery = new MessageDelivery(key, savedMessage, receiver, false, null);
            messageDeliveryRepository.save(messageDelivery);
        }
        return new MessageResponseDto(savedMessage);
    }

    @Transactional
    public Page<MessageResponseForReadingDto> readMessages(Long chatId, PageableRequest pageableRequest) {
        var currentUser = userService.getCurrentUser();
        var chat = getChatOrThrowException(chatId, currentUser.getId());
        var pageable = getPageable(pageableRequest, "creationTimestamp");
        var receivedMessages = messageRepository.findAllByChat(chat, pageable);
        updateMessageDeliveriesIfNecessary(receivedMessages, currentUser);
        var users = chat.getUsers();
        var messagesWithDeliveries = receivedMessages.stream().map((message) -> {
            var messageDeliveries = users.stream().map((user) -> {
                var key = new MessageDeliveryKey(message.getId(), user.getId());
                return getMessageDeliveryOrNull(key);
            }).filter(Objects::nonNull).toList();
            return new MessageResponseForReadingDto(message, messageDeliveries);
        }).toList();
        return new PageImpl<>(messagesWithDeliveries, pageable, receivedMessages.getTotalElements());
    }

    private void updateMessageDeliveriesIfNecessary(Page<Message> receivedMessages, User currentUser) {
        for (Message receivedMessage : receivedMessages) {
            if (!receivedMessage.getSender().equals(currentUser)) {
                var key = new MessageDeliveryKey(receivedMessage.getId(), currentUser.getId());
                var messageDelivery = getMessageDeliveryOrThrowException(key);
                if (!messageDelivery.getDelivered()) {
                    messageDelivery.setDelivered(true);
                    messageDelivery.setDeliveryTimestamp(Instant.now());
                    messageDeliveryRepository.save(messageDelivery);
                }
            }
        }
    }

    private MessageDelivery getMessageDeliveryOrThrowException(MessageDeliveryKey key) {
        var messageDelivery = messageDeliveryRepository.findById(key);
        if (messageDelivery.isEmpty())
            throw new ResponseStatusException(NOT_FOUND, "Message delivery is not found for the key " + key);
        else return messageDelivery.get();
    }

    private MessageDelivery getMessageDeliveryOrNull(MessageDeliveryKey key) {
        var messageDelivery = messageDeliveryRepository.findById(key);
        return messageDelivery.orElse(null);
    }

    private static List<User> getReceivers(Chat chat, User currentUser) {
        return chat.getUsers().stream().filter(userId -> !userId.getId().equals(currentUser.getId())).toList();
    }

    private Chat getChatOrThrowException(Long chatId, Long userId) {
        var chatOptional = chatRepository.findById(chatId);
        if (chatOptional.isEmpty()) throw new ResponseStatusException(NOT_FOUND, "Chat is not found");
        else {
            var chat = chatOptional.get();
            if (!isChatAllowedForUser(chat, userId)) throw new ResponseStatusException(BAD_REQUEST,
                    "The user can't send/read messages in the chat " + chat.getName());
            return chat;
        }
    }

    private boolean isChatAllowedForUser(Chat chat, Long userId) {
        return chat.getUsers().stream().anyMatch(user -> user.getId().equals(userId));
    }

    public MessageResponseDto update(Long id, Long chatId, MessageRequestDto message) {
        var originalMessage = getMessageOrThrowException(id);
        var currentUser = userService.getCurrentUser();
        var chat = getChatOrThrowException(chatId, currentUser.getId());
        try {
            var messageEntity = message.toMessageEntity(id, originalMessage.getSender(), chat);
            return new MessageResponseDto(messageRepository.save(messageEntity));
        } catch (Exception ex) {
            throw new ResponseStatusException(BAD_REQUEST, ex.getMessage(), ex);
        }
    }

    private Message getMessageOrThrowException(Long messageId) {
        var message = messageRepository.findById(messageId);
        if (message.isEmpty()) throw new ResponseStatusException(NOT_FOUND, "Message is not found");
        else return message.get();
    }

    public MessageResponseDto delete(Long messageId) {
        var message = getMessageOrThrowException(messageId);
        messageRepository.deleteById(messageId);
        return new MessageResponseDto(message);
    }
}
