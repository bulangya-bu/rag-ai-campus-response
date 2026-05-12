package com.example.webshiyan2.service;

import com.example.webshiyan2.config.AppProperties;
import com.example.webshiyan2.entity.ChatMessage;
import com.example.webshiyan2.entity.ChatSession;
import com.example.webshiyan2.entity.KnowledgeDocument;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AiGatewayService {

    private final AppProperties appProperties;
    private final RestClient restClient;

    public AiGatewayService(AppProperties appProperties) {
        this.appProperties = appProperties;
        this.restClient = RestClient.builder()
                .baseUrl(appProperties.getAi().getBaseUrl())
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + appProperties.getAi().getApiKey())
                .build();
    }

    public String generateAnswer(ChatSession session, List<ChatMessage> history, List<KnowledgeDocument> relevantDocuments) {
        List<AiMessage> messages = new ArrayList<>();
        messages.add(new AiMessage("system", buildSystemPrompt(relevantDocuments)));

        history.stream()
                .skip(Math.max(0, history.size() - appProperties.getAi().getMaxContextMessages()))
                .map(chatMessage -> new AiMessage(chatMessage.getRole(), chatMessage.getContent()))
                .forEach(messages::add);

        ChatCompletionRequest request = new ChatCompletionRequest(
                appProperties.getAi().getModel(),
                messages,
                appProperties.getAi().getTemperature()
        );

        try {
            ChatCompletionResponse response = restClient.post()
                    .uri("/chat/completions")
                    .body(request)
                    .retrieve()
                    .body(ChatCompletionResponse.class);

            String content = extractContent(response);
            if (StringUtils.hasText(content)) {
                return content.trim();
            }
            throw new IllegalStateException("AI 服务返回了空内容");
        } catch (RestClientException ex) {
            return buildFallbackAnswer(session, history, relevantDocuments, ex.getMessage());
        }
    }

    public boolean pingModel() {
        try {
            restClient.get()
                    .uri("/models")
                    .retrieve()
                    .body(Map.class);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    private String buildSystemPrompt(List<KnowledgeDocument> relevantDocuments) {
        String contextBlock;
        if (relevantDocuments.isEmpty()) {
            contextBlock = "当前没有检索到知识库资料，你可以进行一般性回答，但必须明确说明没有命中校园知识库。";
        } else {
            contextBlock = relevantDocuments.stream()
                    .map(this::toContextSnippet)
                    .collect(Collectors.joining("\n\n"));
        }

        return """
                你是“校园 AI 知识问答系统”的智能助教，面向在校学生、辅导员和教务场景提供帮助。
                回答要求：
                1. 优先依据给定知识库内容回答，不能凭空编造具体政策、地点、时间或材料。
                2. 如果知识库信息不足，请明确说“根据当前知识库，暂未找到完整依据”，再给出谨慎建议。
                3. 回答风格要清晰、专业、中文输出，优先用 1-3 条要点组织。
                4. 如果问题与校园业务无关，可以简短回答，但要说明不是基于知识库。
                5. 若知识库中存在办理地点、时间、所需材料，请显式列出。

                以下是本次可用的知识库上下文：
                %s
                """.formatted(contextBlock);
    }

    private String toContextSnippet(KnowledgeDocument document) {
        return """
                [知识条目]
                标题：%s
                分类：%s
                标签：%s
                来源：%s
                摘要：%s
                内容：%s
                """.formatted(
                document.getTitle(),
                document.getCategory(),
                defaultText(document.getTags()),
                defaultText(document.getSource()),
                defaultText(document.getSummary()),
                document.getContent()
        );
    }

    private String extractContent(ChatCompletionResponse response) {
        if (response == null || response.choices() == null || response.choices().isEmpty()) {
            return null;
        }
        Choice choice = response.choices().getFirst();
        if (choice.message() == null) {
            return null;
        }
        return choice.message().content();
    }

    private String buildFallbackAnswer(ChatSession session, List<ChatMessage> history, List<KnowledgeDocument> relevantDocuments, String errorMessage) {
        String lastUserQuestion = history.isEmpty() ? "" : history.getLast().getContent();
        StringBuilder builder = new StringBuilder();
        builder.append("当前大模型服务暂时不可用，我先根据已命中的校园知识库给出参考。");

        if (StringUtils.hasText(lastUserQuestion)) {
            builder.append("\n问题：").append(lastUserQuestion);
        }

        if (relevantDocuments.isEmpty()) {
            builder.append("\n当前没有检索到可直接引用的知识条目，请稍后重试，或先在知识库中补充相关资料。");
        } else {
            builder.append("\n已命中的知识条目如下：");
            for (KnowledgeDocument document : relevantDocuments) {
                builder.append("\n- ").append(document.getTitle()).append("：")
                        .append(defaultText(document.getSummary(), document.getContent()));
            }
        }

        if (StringUtils.hasText(errorMessage)) {
            builder.append("\n\n技术说明：AI 网关调用失败（").append(errorMessage).append("）。");
        }
        return builder.toString();
    }

    private String defaultText(String value) {
        return StringUtils.hasText(value) ? value : "未提供";
    }

    private String defaultText(String preferred, String fallback) {
        if (StringUtils.hasText(preferred)) {
            return preferred;
        }
        if (!StringUtils.hasText(fallback)) {
            return "未提供";
        }
        return fallback.length() <= 80 ? fallback : fallback.substring(0, 80) + "...";
    }

    private record ChatCompletionRequest(
            String model,
            List<AiMessage> messages,
            double temperature
    ) {
    }

    private record AiMessage(String role, String content) {
    }

    private record ChatCompletionResponse(List<Choice> choices) {
    }

    private record Choice(ResponseMessage message) {
    }

    private record ResponseMessage(String content) {
    }
}
