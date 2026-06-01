package com.example.webshiyan2.repository;

import com.example.webshiyan2.entity.AiModelConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AiModelConfigRepository extends JpaRepository<AiModelConfig, Long> {

    List<AiModelConfig> findAllByOrderByActiveDescUpdatedAtDesc();

    Optional<AiModelConfig> findFirstByActiveTrue();
}
