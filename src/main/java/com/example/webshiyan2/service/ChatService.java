package com.example.webshiyan2.service;

import com.example.webshiyan2.dto.ChatConversationResponse;
import com.example.webshiyan2.dto.ChatExchangeResponse;
import com.example.webshiyan2.dto.ChatMessageRequest;
import com.example.webshiyan2.dto.ChatMessageResponse;
import com.example.webshiyan2.dto.ChatSessionCreateRequest;
import com.example.webshiyan2.dto.ChatSessionResponse;
import com.example.webshiyan2.dto.KnowledgeDocumentResponse;
import com.example.webshiyan2.entity.ChatMessage;
import com.example.webshiyan2.entity.ChatSession;
import com.example.webshiyan2.entity.KnowledgeDocument;
import com.example.webshiyan2.exception.ResourceNotFoundException;
import com.example.webshiyan2.repository.ChatMessageRepository;
import com.example.webshiyan2.repository.ChatSessionRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatService {

    private static final DateTimeFormatter SESSION_TIME_FORMATTER = DateTimeFormatter.ofPattern("MM-dd HH:mm");

    private final ChatSessionRepository chatSessionRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final KnowledgeService knowledgeService;
    private final AiGatewayService aiGatewayService;

    public ChatService(ChatSessionRepository chatSessionRepository,
                       ChatMessageRepository chatMessageRepository,
                       KnowledgeService knowledgeService,
                       AiGatewayService aiGatewayService) {
        this.chatSessionRepository = chatSessionRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.knowledgeService = knowledgeService;
        this.aiGatewayService = aiGatewayService;
    }

    public List<ChatSessionResponse> listSessions() {
        return listSessions(20);
    }

    public List<ChatSessionResponse> listSessions(int limit) {
        return chatSessionRepository.findAllByOrderByUpdatedAtDesc().stream()
                .limit(limit)
                .map(this::toSessionResponse)
                .toList();
    }

    public ChatSessionResponse createSession(ChatSessionCreateRequest request) {
        ChatSession session = new ChatSession();
        session.setTitle(resolveTitle(request));
        return toSessionResponse(chatSessionRepository.save(session));
    }

    public ChatConversationResponse getConversation(Long sessionId) {
        ChatSession session = findSession(sessionId);
        List<ChatMessageResponse> messages = chatMessageRepository.findBySession_IdOrderByCreatedAtAsc(sessionId).stream()
                .map(this::toMessageResponse)
                .toList();
        List<KnowledgeDocumentResponse> references = messages.stream()
                .filter(message -> "assistant".equals(message.role()) && !message.citationIds().isEmpty())
                .reduce((first, second) -> second)
                .map(message -> knowledgeService.getDocumentsByIds(message.citationIds()))
                .orElse(List.of());
        return new ChatConversationResponse(toSessionResponse(session), messages, references);
    }

    public ChatExchangeResponse sendMessage(Long sessionId, ChatMessageRequest request) {
        ChatSession session = findSession(sessionId);

        ChatMessage userMessage = saveMessage(session, "user", request.content().trim(), Collections.emptyList());
        maybeRenameSession(session, request.content());

        List<KnowledgeDocument> relevantDocuments = knowledgeService.findRelevantDocuments(request.content(), 4);
        List<ChatMessage> history = chatMessageRepository.findBySession_IdOrderByCreatedAtAsc(sessionId);
        String assistantContent = aiGatewayService.generateAnswer(session, history, relevantDocuments);
        ChatMessage assistantMessage = saveMessage(session, "assistant", assistantContent, relevantDocuments);

        session.setUpdatedAt(LocalDateTime.now());
        chatSessionRepository.save(session);

        List<KnowledgeDocumentResponse> references = relevantDocuments.stream()
                .map(knowledgeService::toResponse)
                .toList();

        return new ChatExchangeResponse(
                toSessionResponse(session),
                toMessageResponse(userMessage),
                toMessageResponse(assistantMessage),
                references,
                relevantDocuments.isEmpty() ? "general-chat" : "knowledge-grounded"
        );
    }

    @Transactional
    public void deleteSession(Long sessionId) {
        ChatSession session = findSession(sessionId);
        List<ChatMessage> messages = chatMessageRepository.findBySession_IdOrderByCreatedAtAsc(sessionId);
        if (!messages.isEmpty()) {
            chatMessageRepository.deleteAll(messages);
        }
        chatSessionRepository.delete(session);
    }

    public long countSessions() {
        return chatSessionRepository.count();
    }

    public long countMessages() {
        return chatMessageRepository.count();
    }

    private ChatSession findSession(Long sessionId) {
        return chatSessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("未找到会话，id=" + sessionId));
    }

    private ChatMessage saveMessage(ChatSession session, String role, String content, List<KnowledgeDocument> citations) {
        ChatMessage message = new ChatMessage();
        message.setSession(session);
        message.setRole(role);
        message.setContent(content);

        if (!citations.isEmpty()) {
            message.setCitationIds(citations.stream().map(KnowledgeDocument::getId).map(String::valueOf).collect(Collectors.joining(",")));
            message.setCitationTitles(citations.stream().map(KnowledgeDocument::getTitle).collect(Collectors.joining("||")));
        }

        return chatMessageRepository.save(message);
    }

    private void maybeRenameSession(ChatSession session, String firstQuestion) {
        if (!StringUtils.hasText(session.getTitle()) || session.getTitle().startsWith("新会话")) {
            String compact = firstQuestion.replaceAll("\\s+", " ").trim();
            if (compact.length() > 18) {
                compact = compact.substring(0, 18) + "...";
            }
            session.setTitle(compact);
            chatSessionRepository.save(session);
        }
    }

    private String resolveTitle(ChatSessionCreateRequest request) {
        if (request != null && StringUtils.hasText(request.title())) {
            return request.title().trim();
        }
        return "新会话 " + LocalDateTime.now().format(SESSION_TIME_FORMATTER);
    }

    private ChatSessionResponse toSessionResponse(ChatSession session) {
        long messageCount = chatMessageRepository.countBySession_Id(session.getId());
        String lastPreview = chatMessageRepository.findFirstBySession_IdOrderByCreatedAtDesc(session.getId())
                .map(ChatMessage::getContent)
                .map(content -> content.length() > 40 ? content.substring(0, 40) + "..." : content)
                .orElse("暂无消息");

        return new ChatSessionResponse(
                session.getId(),
                session.getTitle(),
                session.getCreatedAt(),
                session.getUpdatedAt(),
                messageCount,
                lastPreview
        );
    }

    private ChatMessageResponse toMessageResponse(ChatMessage message) {
        List<String> citations = StringUtils.hasText(message.getCitationTitles())
                ? List.of(message.getCitationTitles().split("\\|\\|"))
                : List.of();
        List<Long> citationIds = StringUtils.hasText(message.getCitationIds())
                ? List.of(message.getCitationIds().split(",")).stream().map(Long::valueOf).toList()
                : List.of();

        return new ChatMessageResponse(
                message.getId(),
                message.getRole(),
                message.getContent(),
                message.getCreatedAt(),
                citations,
                citationIds
        );
    }
}
