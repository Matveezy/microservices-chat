package com.lab2.chat.repository;

import com.lab2.chat.entity.Chat;
import com.lab2.chat.entity.ChatParticipant;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Repository
public interface ChatRepository extends R2dbcRepository<Chat, Long> {

    Mono<Boolean> existsByName(String name);

    Mono<Boolean> existsByNameAndIsPrivate(String name, boolean isPrivate);

    Mono<Chat> findByNameAndIsPrivate(String name, boolean isPrivate);

    @Query("SELECT c.* FROM chats c " +
           "JOIN chat_participants cp ON c.id = cp.chat_id " +
           "WHERE c.private = :isPrivate AND cp.user_id = :userId")
    Flux<Chat> findByIsPrivateAndUserIds(boolean isPrivate, Long userId);

    @Query("SELECT * FROM chat_participants WHERE chat_id = :chatId")
    Flux<ChatParticipant> findParticipantsByChatId(Long chatId);
}
