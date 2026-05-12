package com.example.webshiyan2.dto;

import java.util.List;

public record ChatConversationResponse(
        ChatSessionResponse session,
        List<ChatMessageResponse> messages,
        List<KnowledgeDocumentResponse> references
) {
}
