package com.example.webshiyan2.repository;

import com.example.webshiyan2.entity.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatSessionRepository extends JpaRepository<ChatSession, Long> {

    List<ChatSession> findAllByOrderByUpdatedAtDesc();
}
