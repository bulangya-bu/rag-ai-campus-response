package com.example.webshiyan2.service;

import com.example.webshiyan2.config.AppProperties;
import com.example.webshiyan2.dto.DashboardOverviewResponse;
import com.example.webshiyan2.dto.SystemStatusResponse;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    private final ChatService chatService;
    private final KnowledgeService knowledgeService;
    private final AiGatewayService aiGatewayService;
    private final AppProperties appProperties;

    public DashboardService(ChatService chatService,
                            KnowledgeService knowledgeService,
                            AiGatewayService aiGatewayService,
                            AppProperties appProperties) {
        this.chatService = chatService;
        this.knowledgeService = knowledgeService;
        this.aiGatewayService = aiGatewayService;
        this.appProperties = appProperties;
    }

    public DashboardOverviewResponse getOverview() {
        return new DashboardOverviewResponse(
                chatService.countSessions(),
                chatService.countMessages(),
                knowledgeService.countDocuments(),
                knowledgeService.featuredDocuments(4),
                chatService.listSessions(6),
                getSystemStatus()
        );
    }

    public SystemStatusResponse getSystemStatus() {
        return new SystemStatusResponse(
                "MySQL(localhost:3306/campus_ai_qa)",
                appProperties.getAi().getModel(),
                appProperties.getAi().getBaseUrl(),
                aiGatewayService.pingModel(),
                java.time.LocalDateTime.now()
        );
    }
}
