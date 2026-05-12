package com.example.webshiyan2.repository;

import com.example.webshiyan2.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findBySession_IdOrderByCreatedAtAsc(Long sessionId);

    Optional<ChatMessage> findFirstBySession_IdOrderByCreatedAtDesc(Long sessionId);

    long countBySession_Id(Long sessionId);
}
