package com.example.webshiyan2.dto;

import java.time.LocalDateTime;

public record SystemStatusResponse(
        String database,
        Long activeConfigId,
        String activeConfigName,
        String model,
        String baseUrl,
        boolean aiReady,
        LocalDateTime checkedAt
) {
}
