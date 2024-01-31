package org.example.repository;

import org.example.domain.model.Chat;
import org.example.domain.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {

    boolean existsByName(String name);

    boolean existsByNameAndPrivateB(String name, boolean isPrivate);

    Optional<Chat> findByNameAndPrivateB(String name, boolean isPrivate);

    Page<Chat> findByPrivateBAndUsersContaining(boolean isPrivate, User userId, Pageable pageable);
}