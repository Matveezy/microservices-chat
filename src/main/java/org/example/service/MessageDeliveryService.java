package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.domain.dto.MessageDeliveryResponseDto;
import org.example.domain.dto.PageableRequest;
import org.example.domain.model.MessageDelivery;
import org.example.domain.model.MessageDeliveryKey;
import org.example.repository.MessageDeliveryRepository;
import org.example.repository.MessageRepository;
import org.example.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import static org.example.domain.dto.PageableRequest.getPageable;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class MessageDeliveryService {

    private final MessageDeliveryRepository messageDeliveryRepository;

    private final MessageRepository messageRepository;

    private final UserRepository userRepository;

    public Page<MessageDeliveryResponseDto> findAll(PageableRequest pageableRequest) {
        var pageable = getPageable(pageableRequest);
        var messagesDeliveries = messageDeliveryRepository.findAll(pageable);
        var messagesDeliveriesResponse = messagesDeliveries.stream().map(MessageDeliveryResponseDto::new).toList();
        return new PageImpl<>(messagesDeliveriesResponse, pageable, messagesDeliveries.getTotalElements());
    }

    public MessageDeliveryResponseDto create(Long messageId, Long receiverId) {
        var message = messageRepository.findById(messageId);
        if (message.isEmpty())
            throw new ResponseStatusException(NOT_FOUND, "Message is not found");
        var receiver = userRepository.findById(receiverId);
        if (receiver.isEmpty())
            throw new ResponseStatusException(NOT_FOUND, "Receiver is not found");
        var key = new MessageDeliveryKey(messageId, receiverId);
        var messageDeliveryEntity = new MessageDelivery(key, message.get(), receiver.get(), false, null);
        return new MessageDeliveryResponseDto(messageDeliveryRepository.save(messageDeliveryEntity));
    }

    private MessageDelivery getMessageDeliveryOrThrowException(MessageDeliveryKey key) {
        var messageDelivery = messageDeliveryRepository.findById(key);
        if (messageDelivery.isEmpty())
            throw new ResponseStatusException(NOT_FOUND, "Message delivery is not found for the key " + key);
        else return messageDelivery.get();
    }

    public MessageDeliveryResponseDto delete(Long messageId, Long receiverId) {
        var key = new MessageDeliveryKey(messageId, receiverId);
        var messageDelivery = getMessageDeliveryOrThrowException(key);
        messageDeliveryRepository.deleteById(key);
        return new MessageDeliveryResponseDto(messageDelivery);
    }
}
