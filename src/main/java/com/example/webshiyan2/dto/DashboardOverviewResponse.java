package com.example.webshiyan2.dto;

import java.util.List;

public record DashboardOverviewResponse(
        long sessionCount,
        long messageCount,
        long knowledgeCount,
        List<KnowledgeDocumentResponse> featuredKnowledge,
        List<ChatSessionResponse> recentSessions,
        SystemStatusResponse systemStatus
) {
}
