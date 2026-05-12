package com.example.webshiyan2.dto;

import java.time.LocalDateTime;

public record KnowledgeDocumentResponse(
        Long id,
        String title,
        String category,
        String tags,
        String summary,
        String content,
        String source,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
