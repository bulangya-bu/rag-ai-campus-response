package com.example.webshiyan2.dto;

import java.util.List;

public record ModelConfigTestResponse(
        boolean reachable,
        boolean modelAvailable,
        String message,
        List<String> availableModels
) {
}
