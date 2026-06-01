package com.example.webshiyan2.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ModelConfigTestRequest(
        Long configId,
        @NotBlank(message = "模型服务 URL 不能为空")
        @Size(max = 255, message = "模型服务 URL 不能超过255个字符")
        String baseUrl,
        @Size(max = 255, message = "API Key 不能超过255个字符")
        String apiKey,
        @NotBlank(message = "模型名不能为空")
        @Size(max = 120, message = "模型名不能超过120个字符")
        String model
) {
}
