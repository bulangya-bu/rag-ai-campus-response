package com.example.webshiyan2.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record KnowledgeDocumentRequest(
        @NotBlank(message = "文档标题不能为空")
        @Size(max = 120, message = "文档标题不能超过120个字符")
        String title,
        @NotBlank(message = "分类不能为空")
        @Size(max = 60, message = "分类不能超过60个字符")
        String category,
        @Size(max = 200, message = "标签不能超过200个字符")
        String tags,
        @Size(max = 1000, message = "摘要不能超过1000个字符")
        String summary,
        @NotBlank(message = "知识内容不能为空")
        String content,
        @Size(max = 120, message = "来源不能超过120个字符")
        String source
) {
}
