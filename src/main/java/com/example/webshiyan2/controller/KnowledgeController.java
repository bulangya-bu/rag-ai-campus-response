package com.example.webshiyan2.controller;

import com.example.webshiyan2.dto.ApiResponse;
import com.example.webshiyan2.dto.KnowledgeDocumentRequest;
import com.example.webshiyan2.dto.KnowledgeDocumentResponse;
import com.example.webshiyan2.service.KnowledgeService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/knowledge-documents")
public class KnowledgeController {

    private final KnowledgeService knowledgeService;

    public KnowledgeController(KnowledgeService knowledgeService) {
        this.knowledgeService = knowledgeService;
    }

    @GetMapping
    public ApiResponse<List<KnowledgeDocumentResponse>> listDocuments(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String category
    ) {
        return ApiResponse.success(knowledgeService.listDocuments(query, category));
    }

    @GetMapping("/{id}")
    public ApiResponse<KnowledgeDocumentResponse> getDocument(@PathVariable Long id) {
        return ApiResponse.success(knowledgeService.getDocument(id));
    }

    @PostMapping
    public ApiResponse<KnowledgeDocumentResponse> createDocument(@Valid @RequestBody KnowledgeDocumentRequest request) {
        return ApiResponse.success("知识文档已创建", knowledgeService.createDocument(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<KnowledgeDocumentResponse> updateDocument(@PathVariable Long id,
                                                                 @Valid @RequestBody KnowledgeDocumentRequest request) {
        return ApiResponse.success("知识文档已更新", knowledgeService.updateDocument(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteDocument(@PathVariable Long id) {
        knowledgeService.deleteDocument(id);
        return ApiResponse.<Void>success("知识文档已删除", null);
    }
}
