package com.lab2.message.service;

import com.lab2.message.entity.Message;
import com.lab2.message.entity.MessageDelivery;
import com.lab2.message.entity.MessageDeliveryKey;
import com.lab2.message.repository.MessageDeliveryRepository;
import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MessageDeliveryService {

    private final MessageDeliveryRepository messageDeliveryRepository;

    public Optional<MessageDelivery> findByKey(MessageDeliveryKey messageDeliveryKey) {
        return messageDeliveryRepository.findById(messageDeliveryKey);
    }

    @Transactional
    public MessageDelivery create(Long receiverId, Message message) {
        MessageDelivery messageDelivery = MessageDelivery.builder()
                .messageDeliveryKey(MessageDeliveryKey.builder().receiverId(receiverId).messageId(message.getId()).build())
                .message(message)
                .delivered(false)
                .deliveryTimestamp(null).build();
        return messageDeliveryRepository.save(messageDelivery);
    }

    @Transactional
    public void updateMessageDeliveriesIfNecessary(List<Message> receivedMessages, Long userId) {
        receivedMessages.stream()
                .filter(message -> !(message.getSenderId().equals(userId)))
                .forEach(message -> {
                    System.out.println(message);
                    Optional<MessageDelivery> messageDeliveryOptional = messageDeliveryRepository.findById(MessageDeliveryKey.builder().receiverId(userId).messageId(message.getId()).build());
                    System.out.println(messageDeliveryOptional.get());
                    messageDeliveryOptional.map(messageDelivery -> {
                        if (!messageDelivery.getDelivered()) {
                            messageDelivery.setDelivered(true);
                            messageDelivery.setDeliveryTimestamp(Instant.now());
                            messageDeliveryRepository.save(messageDelivery);
                        }
                        return messageDelivery;
                    });
                });
    }

    @Transactional
    public void delete(Message message, Long receiverId) {
        MessageDeliveryKey key = MessageDeliveryKey.builder().messageId(message.getId()).receiverId(receiverId).build();
        messageDeliveryRepository.findById(key).orElseThrow(EntityExistsException::new);
        messageDeliveryRepository.deleteById(key);
    }
}
