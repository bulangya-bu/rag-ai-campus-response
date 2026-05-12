package com.example.webshiyan2.controller;

import com.example.webshiyan2.dto.ApiResponse;
import com.example.webshiyan2.dto.ChatConversationResponse;
import com.example.webshiyan2.dto.ChatExchangeResponse;
import com.example.webshiyan2.dto.ChatMessageRequest;
import com.example.webshiyan2.dto.ChatSessionCreateRequest;
import com.example.webshiyan2.dto.ChatSessionResponse;
import com.example.webshiyan2.service.ChatService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/sessions")
    public ApiResponse<List<ChatSessionResponse>> listSessions() {
        return ApiResponse.success(chatService.listSessions());
    }

    @PostMapping("/sessions")
    public ApiResponse<ChatSessionResponse> createSession(@RequestBody(required = false) ChatSessionCreateRequest request) {
        return ApiResponse.success("会话已创建", chatService.createSession(request == null ? new ChatSessionCreateRequest(null) : request));
    }

    @GetMapping("/sessions/{sessionId}")
    public ApiResponse<ChatConversationResponse> getConversation(@PathVariable Long sessionId) {
        return ApiResponse.success(chatService.getConversation(sessionId));
    }

    @PostMapping("/sessions/{sessionId}/messages")
    public ApiResponse<ChatExchangeResponse> sendMessage(@PathVariable Long sessionId,
                                                         @Valid @RequestBody ChatMessageRequest request) {
        return ApiResponse.success("消息发送成功", chatService.sendMessage(sessionId, request));
    }

    @DeleteMapping("/sessions/{sessionId}")
    public ApiResponse<Void> deleteSession(@PathVariable Long sessionId) {
        chatService.deleteSession(sessionId);
        return ApiResponse.<Void>success("会话已删除", null);
    }
}
