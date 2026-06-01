package com.example.webshiyan2.dto;

import java.time.LocalDateTime;

public record ModelConfigResponse(
        Long id,
        String name,
        String baseUrl,
        String model,
        String maskedApiKey,
        boolean hasApiKey,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
