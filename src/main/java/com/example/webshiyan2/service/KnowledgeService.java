package com.example.webshiyan2.service;

import com.example.webshiyan2.dto.KnowledgeDocumentRequest;
import com.example.webshiyan2.dto.KnowledgeDocumentResponse;
import com.example.webshiyan2.entity.KnowledgeDocument;
import com.example.webshiyan2.exception.ResourceNotFoundException;
import com.example.webshiyan2.repository.KnowledgeDocumentRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Locale;
import java.util.Set;

@Service
public class KnowledgeService {

    private final KnowledgeDocumentRepository knowledgeDocumentRepository;

    public KnowledgeService(KnowledgeDocumentRepository knowledgeDocumentRepository) {
        this.knowledgeDocumentRepository = knowledgeDocumentRepository;
    }

    public List<KnowledgeDocumentResponse> listDocuments(String query, String category) {
        List<KnowledgeDocument> candidates = StringUtils.hasText(category)
                ? knowledgeDocumentRepository.findByCategoryIgnoreCaseOrderByUpdatedAtDesc(category.trim())
                : knowledgeDocumentRepository.findAllByOrderByUpdatedAtDesc();

        if (!StringUtils.hasText(query)) {
            return candidates.stream().map(this::toResponse).toList();
        }

        return candidates.stream()
                .map(document -> new ScoredDocument(document, scoreDocument(query, document)))
                .filter(scored -> scored.score() > 0)
                .sorted(Comparator.comparingInt(ScoredDocument::score).reversed()
                        .thenComparing(scored -> scored.document().getUpdatedAt(),
                                Comparator.nullsLast(Comparator.reverseOrder())))
                .map(scored -> toResponse(scored.document()))
                .toList();
    }

    public KnowledgeDocumentResponse getDocument(Long id) {
        return toResponse(findEntity(id));
    }

    public KnowledgeDocumentResponse createDocument(KnowledgeDocumentRequest request) {
        KnowledgeDocument document = new KnowledgeDocument();
        apply(document, request);
        return toResponse(knowledgeDocumentRepository.save(document));
    }

    public KnowledgeDocumentResponse updateDocument(Long id, KnowledgeDocumentRequest request) {
        KnowledgeDocument document = findEntity(id);
        apply(document, request);
        return toResponse(knowledgeDocumentRepository.save(document));
    }

    public void deleteDocument(Long id) {
        KnowledgeDocument document = findEntity(id);
        knowledgeDocumentRepository.delete(document);
    }

    public long countDocuments() {
        return knowledgeDocumentRepository.count();
    }

    public List<KnowledgeDocumentResponse> featuredDocuments(int limit) {
        return knowledgeDocumentRepository.findAllByOrderByUpdatedAtDesc().stream()
                .limit(limit)
                .map(this::toResponse)
                .toList();
    }

    public List<KnowledgeDocumentResponse> getDocumentsByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        Map<Long, KnowledgeDocument> documents = new HashMap<>();
        knowledgeDocumentRepository.findAllById(ids).forEach(document -> documents.put(document.getId(), document));
        return ids.stream()
                .map(documents::get)
                .filter(document -> document != null)
                .map(this::toResponse)
                .toList();
    }

    public List<KnowledgeDocument> findRelevantDocuments(String question, int limit) {
        List<ScoredDocument> scoredDocuments = knowledgeDocumentRepository.findAllByOrderByUpdatedAtDesc().stream()
                .map(document -> new ScoredDocument(document, scoreDocument(question, document)))
                .filter(scored -> scored.score() > 0)
                .sorted(Comparator.comparingInt(ScoredDocument::score).reversed()
                        .thenComparing(scored -> scored.document().getUpdatedAt(),
                                Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(limit)
                .toList();

        return scoredDocuments.stream().map(ScoredDocument::document).toList();
    }

    public boolean hasAnyDocuments() {
        return knowledgeDocumentRepository.count() > 0;
    }

    public void seedIfEmpty(List<KnowledgeDocument> documents) {
        if (knowledgeDocumentRepository.count() == 0) {
            knowledgeDocumentRepository.saveAll(documents);
        }
    }

    private KnowledgeDocument findEntity(Long id) {
        return knowledgeDocumentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("未找到知识文档，id=" + id));
    }

    private void apply(KnowledgeDocument document, KnowledgeDocumentRequest request) {
        document.setTitle(request.title().trim());
        document.setCategory(request.category().trim());
        document.setTags(trimToNull(request.tags()));
        document.setSummary(trimToNull(request.summary()));
        document.setContent(request.content().trim());
        document.setSource(trimToNull(request.source()));
    }

    public KnowledgeDocumentResponse toResponse(KnowledgeDocument document) {
        return new KnowledgeDocumentResponse(
                document.getId(),
                document.getTitle(),
                document.getCategory(),
                document.getTags(),
                document.getSummary(),
                document.getContent(),
                document.getSource(),
                document.getCreatedAt(),
                document.getUpdatedAt()
        );
    }

    private int scoreDocument(String question, KnowledgeDocument document) {
        String normalizedQuestion = normalize(question);
        if (normalizedQuestion.isBlank()) {
            return 0;
        }

        String haystack = normalize(String.join(" ",
                nullSafe(document.getTitle()),
                nullSafe(document.getCategory()),
                nullSafe(document.getTags()),
                nullSafe(document.getSummary()),
                nullSafe(document.getContent()),
                nullSafe(document.getSource())
        ));

        int score = 0;
        if (haystack.contains(normalizedQuestion)) {
            score += 12;
        }

        for (String token : extractTokens(question)) {
            if (haystack.contains(token)) {
                score += token.length() >= 4 ? 4 : 2;
            }
        }

        for (String window : extractCharacterWindows(question)) {
            if (haystack.contains(window)) {
                score += 1;
            }
        }

        return score;
    }

    private List<String> extractTokens(String text) {
        return Arrays.stream(normalize(text).split("[\\s,，。！？；;：:/()（）\\-]+"))
                .map(String::trim)
                .filter(token -> token.length() >= 2)
                .distinct()
                .limit(12)
                .toList();
    }

    private List<String> extractCharacterWindows(String text) {
        String compact = normalize(text).replace(" ", "");
        Set<String> windows = new LinkedHashSet<>();
        for (int size = 2; size <= 4; size++) {
            for (int index = 0; index + size <= compact.length(); index++) {
                windows.add(compact.substring(index, index + size));
                if (windows.size() >= 16) {
                    return new ArrayList<>(windows);
                }
            }
        }
        return new ArrayList<>(windows);
    }

    private String normalize(String value) {
        return nullSafe(value).toLowerCase(Locale.ROOT);
    }

    private String nullSafe(String value) {
        return value == null ? "" : value;
    }

    private String trimToNull(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }

    private record ScoredDocument(KnowledgeDocument document, int score) {
    }
}
