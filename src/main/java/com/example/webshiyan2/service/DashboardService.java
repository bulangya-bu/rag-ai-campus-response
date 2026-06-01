package com.example.webshiyan2.service;

import com.example.webshiyan2.config.AppProperties;
import com.example.webshiyan2.dto.DashboardOverviewResponse;
import com.example.webshiyan2.dto.ModelConfigResponse;
import com.example.webshiyan2.dto.SystemStatusResponse;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    private final ChatService chatService;
    private final KnowledgeService knowledgeService;
    private final AiGatewayService aiGatewayService;
    private final ModelConfigService modelConfigService;

    public DashboardService(ChatService chatService,
                            KnowledgeService knowledgeService,
                            AiGatewayService aiGatewayService,
                            ModelConfigService modelConfigService) {
        this.chatService = chatService;
        this.knowledgeService = knowledgeService;
        this.aiGatewayService = aiGatewayService;
        this.modelConfigService = modelConfigService;
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
        ModelConfigResponse activeConfig = modelConfigService.getActiveConfig();
        return new SystemStatusResponse(
                "MySQL(localhost:3306/campus_ai_qa)",
                activeConfig.id(),
                activeConfig.name(),
                activeConfig.model(),
                activeConfig.baseUrl(),
                aiGatewayService.pingModel(),
                java.time.LocalDateTime.now()
        );
    }
}
