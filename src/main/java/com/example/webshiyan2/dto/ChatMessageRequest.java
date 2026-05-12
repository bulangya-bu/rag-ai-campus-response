package com.example.webshiyan2.dto;

import jakarta.validation.constraints.NotBlank;

public record ChatMessageRequest(
        @NotBlank(message = "消息内容不能为空")
        String content
) {
}
