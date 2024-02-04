package com.lab2.chat.repository;

import com.lab2.chat.entity.Chat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {

    boolean existsByName(String name);

    boolean existsByNameAndIsPrivate(String name, boolean isPrivate);

    Optional<Chat> findByNameAndIsPrivate(String name, boolean isPrivate);

    Optional<Chat> findByIsPrivateAndUserIdsContaining(boolean isPrivate, Long userId);
}
