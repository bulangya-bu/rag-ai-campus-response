package com.example.webshiyan2.controller;

import com.example.webshiyan2.dto.ApiResponse;
import com.example.webshiyan2.dto.ModelConfigRequest;
import com.example.webshiyan2.dto.ModelConfigResponse;
import com.example.webshiyan2.dto.ModelConfigTestRequest;
import com.example.webshiyan2.dto.ModelConfigTestResponse;
import com.example.webshiyan2.service.ModelConfigService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/model-configs")
public class ModelConfigController {

    private final ModelConfigService modelConfigService;

    public ModelConfigController(ModelConfigService modelConfigService) {
        this.modelConfigService = modelConfigService;
    }

    @GetMapping
    public ApiResponse<List<ModelConfigResponse>> listConfigs() {
        return ApiResponse.success(modelConfigService.listConfigs());
    }

    @GetMapping("/active")
    public ApiResponse<ModelConfigResponse> getActiveConfig() {
        return ApiResponse.success(modelConfigService.getActiveConfig());
    }

    @PostMapping
    public ApiResponse<ModelConfigResponse> createConfig(@Valid @RequestBody ModelConfigRequest request) {
        return ApiResponse.success("模型配置已创建", modelConfigService.createConfig(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<ModelConfigResponse> updateConfig(@PathVariable Long id,
                                                         @Valid @RequestBody ModelConfigRequest request) {
        return ApiResponse.success("模型配置已更新", modelConfigService.updateConfig(id, request));
    }

    @PostMapping("/{id}/activate")
    public ApiResponse<ModelConfigResponse> activateConfig(@PathVariable Long id) {
        return ApiResponse.success("当前激活模型已切换", modelConfigService.activateConfig(id));
    }

    @PostMapping("/test")
    public ApiResponse<ModelConfigTestResponse> testConfig(@Valid @RequestBody ModelConfigTestRequest request) {
        return ApiResponse.success(modelConfigService.testConnection(request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteConfig(@PathVariable Long id) {
        modelConfigService.deleteConfig(id);
        return ApiResponse.<Void>success("模型配置已删除", null);
    }
}
