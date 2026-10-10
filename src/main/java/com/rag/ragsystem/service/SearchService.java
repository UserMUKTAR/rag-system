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

    public List<String> search(String question, int limit) {

        // 1. Convert the question into an embedding
        List<Double> embedding =
                embeddingService.generateEmbedding(question);

        // 2. Convert the embedding to pgvector format
        String vector = embedding.toString();

        // 3. Find the most similar chunks
        String sql = """
                SELECT content
                FROM document_chunks
                WHERE embedding IS NOT NULL
                ORDER BY embedding <=> ?::vector
                LIMIT ?
                """;

        // 4. Execute the search and return the chunk text
        return jdbcTemplate.query(
                sql,
                (resultSet, rowNumber) -> resultSet.getString("content"),
                vector,
                limit
        );
    }
}