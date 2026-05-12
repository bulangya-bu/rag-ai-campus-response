package com.example.webshiyan2.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ChatMessageResponse(
        Long id,
        String role,
        String content,
        LocalDateTime createdAt,
        List<String> citations,
        List<Long> citationIds
) {
}
