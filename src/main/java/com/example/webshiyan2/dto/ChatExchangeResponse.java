package com.example.webshiyan2.dto;

import java.util.List;

public record ChatExchangeResponse(
        ChatSessionResponse session,
        ChatMessageResponse userMessage,
        ChatMessageResponse assistantMessage,
        List<KnowledgeDocumentResponse> references,
        String answerMode
) {
}
