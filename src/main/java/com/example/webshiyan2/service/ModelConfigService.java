package com.example.webshiyan2.service;

import com.example.webshiyan2.config.AppProperties;
import com.example.webshiyan2.dto.ModelConfigRequest;
import com.example.webshiyan2.dto.ModelConfigResponse;
import com.example.webshiyan2.dto.ModelConfigTestRequest;
import com.example.webshiyan2.dto.ModelConfigTestResponse;
import com.example.webshiyan2.entity.AiModelConfig;
import com.example.webshiyan2.exception.ResourceNotFoundException;
import com.example.webshiyan2.repository.AiModelConfigRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class ModelConfigService {

    private final AiModelConfigRepository aiModelConfigRepository;

    public ModelConfigService(AiModelConfigRepository aiModelConfigRepository) {
        this.aiModelConfigRepository = aiModelConfigRepository;
    }

    public List<ModelConfigResponse> listConfigs() {
        return aiModelConfigRepository.findAllByOrderByActiveDescUpdatedAtDesc().stream()
                .map(this::toResponse)
                .toList();
    }

    public ModelConfigResponse getActiveConfig() {
        return toResponse(getActiveEntity());
    }

    public AiModelConfig getActiveEntity() {
        return aiModelConfigRepository.findFirstByActiveTrue()
                .orElseThrow(() -> new IllegalStateException("当前没有激活的模型配置，请先在模型设置中激活一套模型。"));
    }

    public ModelConfigResponse createConfig(ModelConfigRequest request) {
        AiModelConfig config = new AiModelConfig();
        apply(config, request, true);
        config.setActive(aiModelConfigRepository.count() == 0);
        return toResponse(aiModelConfigRepository.save(config));
    }

    public ModelConfigResponse updateConfig(Long id, ModelConfigRequest request) {
        AiModelConfig config = findEntity(id);
        apply(config, request, false);
        return toResponse(aiModelConfigRepository.save(config));
    }

    @Transactional
    public ModelConfigResponse activateConfig(Long id) {
        AiModelConfig target = findEntity(id);
        List<AiModelConfig> configs = aiModelConfigRepository.findAll();
        for (AiModelConfig config : configs) {
            config.setActive(config.getId().equals(target.getId()));
        }
        aiModelConfigRepository.saveAll(configs);
        return toResponse(target);
    }

    public void deleteConfig(Long id) {
        AiModelConfig config = findEntity(id);
        if (config.isActive()) {
            throw new IllegalArgumentException("当前激活配置不能删除，请先切换到其他模型配置。");
        }
        aiModelConfigRepository.delete(config);
    }

    public ModelConfigTestResponse testConnection(ModelConfigTestRequest request) {
        String apiKey = resolveApiKeyForTest(request);
        if (!StringUtils.hasText(apiKey)) {
            throw new IllegalArgumentException("测试连接时必须提供 API Key，或选择一条已有配置沿用其 Key。");
        }

        try {
            ModelListResponse response = buildClient(normalizeBaseUrl(request.baseUrl()), apiKey)
                    .get()
                    .uri("/models")
                    .retrieve()
                    .body(ModelListResponse.class);

            List<String> availableModels = response == null || response.data() == null
                    ? List.of()
                    : response.data().stream()
                    .map(ModelCard::id)
                    .filter(StringUtils::hasText)
                    .toList();

            boolean modelAvailable = availableModels.stream().anyMatch(model -> model.equals(request.model().trim()));
            String message = modelAvailable
                    ? "连接成功，已找到指定模型。"
                    : "连接成功，但模型列表中未找到当前填写的模型名。";

            return new ModelConfigTestResponse(true, modelAvailable, message, availableModels);
        } catch (Exception ex) {
            return new ModelConfigTestResponse(
                    false,
                    false,
                    "连接失败：" + ex.getMessage(),
                    List.of()
            );
        }
    }

    @Transactional
    public void seedDefaultConfig(AppProperties.Ai defaults) {
        if (aiModelConfigRepository.count() == 0) {
            AiModelConfig config = new AiModelConfig();
            config.setName("默认本地模型");
            config.setBaseUrl(normalizeBaseUrl(defaults.getBaseUrl()));
            config.setApiKey(defaults.getApiKey());
            config.setModel(defaults.getModel());
            config.setActive(true);
            aiModelConfigRepository.save(config);
            return;
        }

        if (aiModelConfigRepository.findFirstByActiveTrue().isEmpty()) {
            List<AiModelConfig> configs = aiModelConfigRepository.findAllByOrderByActiveDescUpdatedAtDesc();
            if (!configs.isEmpty()) {
                configs.getFirst().setActive(true);
                aiModelConfigRepository.save(configs.getFirst());
            }
        }
    }

    private AiModelConfig findEntity(Long id) {
        return aiModelConfigRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("未找到模型配置，id=" + id));
    }

    private void apply(AiModelConfig config, ModelConfigRequest request, boolean create) {
        config.setName(request.name().trim());
        config.setBaseUrl(normalizeBaseUrl(request.baseUrl()));
        config.setModel(request.model().trim());

        if (StringUtils.hasText(request.apiKey())) {
            config.setApiKey(request.apiKey().trim());
        } else if (create) {
            throw new IllegalArgumentException("新建模型配置时 API Key 不能为空。");
        }
    }

    private String resolveApiKeyForTest(ModelConfigTestRequest request) {
        if (StringUtils.hasText(request.apiKey())) {
            return request.apiKey().trim();
        }

        if (request.configId() == null) {
            return null;
        }

        return findEntity(request.configId()).getApiKey();
    }

    private RestClient buildClient(String baseUrl, String apiKey) {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .build();
    }

    private String normalizeBaseUrl(String value) {
        String normalized = value.trim();
        while (normalized.endsWith("/")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }
        return normalized;
    }

    private String maskApiKey(String apiKey) {
        if (!StringUtils.hasText(apiKey)) {
            return "";
        }
        if (apiKey.length() <= 4) {
            return "****";
        }
        return apiKey.substring(0, 4) + "••••••";
    }

    private ModelConfigResponse toResponse(AiModelConfig config) {
        return new ModelConfigResponse(
                config.getId(),
                config.getName(),
                config.getBaseUrl(),
                config.getModel(),
                maskApiKey(config.getApiKey()),
                StringUtils.hasText(config.getApiKey()),
                config.isActive(),
                config.getCreatedAt(),
                config.getUpdatedAt()
        );
    }

    private record ModelListResponse(List<ModelCard> data) {
    }

    private record ModelCard(String id) {
    }
}
