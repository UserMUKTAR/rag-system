package com.rag.ragsystem.service;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class SearchService {

    private final JdbcTemplate jdbcTemplate;
    private final EmbeddingService embeddingService;

    public SearchService(
            JdbcTemplate jdbcTemplate,
            EmbeddingService embeddingService) {
        this.jdbcTemplate = jdbcTemplate;
        this.embeddingService = embeddingService;
    }

    public List<SearchResult> search(String question, int limit) {

        List<Double> embedding =
                embeddingService.generateEmbedding(question);

        String vector = embedding.toString();

        String sql = """
                SELECT
                    content,
                    embedding <=> ?::vector AS cosine_distance
                FROM document_chunks
                WHERE embedding IS NOT NULL
                ORDER BY cosine_distance ASC
                LIMIT ?
                """;

        List<SearchResult> results = jdbcTemplate.query(
                sql,
                (resultSet, rowNumber) -> {
                    double distance =
                            resultSet.getDouble("cosine_distance");

                    double similarity = 1.0 - distance;

                    return new SearchResult(
                            resultSet.getString("content"),
                            distance,
                            similarity
                    );
                },
                vector,
                limit
        );

        // Keep only results that meet the provisional threshold.
        return results.stream()
                .filter(result -> result.cosineSimilarity() >= 0.50)
                .toList();
    }

    public record SearchResult(
            String content,
            double cosineDistance,
            double cosineSimilarity) {
    }
}