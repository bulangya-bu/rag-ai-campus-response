package com.example.webshiyan2.repository;

import com.example.webshiyan2.entity.KnowledgeDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KnowledgeDocumentRepository extends JpaRepository<KnowledgeDocument, Long> {

    List<KnowledgeDocument> findAllByOrderByUpdatedAtDesc();

    List<KnowledgeDocument> findByCategoryIgnoreCaseOrderByUpdatedAtDesc(String category);
}
