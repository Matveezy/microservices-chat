package com.lab2.chat.repository;

import com.lab2.chat.entity.ChatParticipant;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public interface ChatParticipantRepository extends R2dbcRepository<ChatParticipant, Long> {

    @Query("SELECT * FROM chat_participants WHERE chat_id = :chatId")
    Flux<ChatParticipant> findAllByChatParticipantKeyChatId(Long chatId);

    @Query("INSERT INTO chat_participants (user_id, chat_id) VALUES (:userId, :chatId)")
    Mono<ChatParticipant> saveChatParticipant(Long userId, Long chatId);

    @Query("DELETE FROM chat_participants WHERE chat_id = :chatId")
    Mono<Void> deleteChatParticipants(Long chatId);

}
