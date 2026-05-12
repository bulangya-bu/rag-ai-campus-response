package com.example.webshiyan2.dto;

import java.time.LocalDateTime;

public record ChatSessionResponse(
        Long id,
        String title,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        long messageCount,
        String lastMessagePreview
) {
}
