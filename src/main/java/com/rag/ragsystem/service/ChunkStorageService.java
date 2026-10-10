package com.rag.ragsystem.service;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class ChunkStorageService {

    private final JdbcTemplate jdbcTemplate;

    public ChunkStorageService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void saveChunk(
            Long documentId,
            int chunkIndex,
            String content,
            List<Double> embedding) {

        if (embedding == null || embedding.size() != 768) {
            throw new IllegalArgumentException(
                    "Embedding must contain exactly 768 dimensions.");
        }

        String vector = embedding.toString();

        String sql = """
                INSERT INTO document_chunks
                    (document_id, chunk_index, content, embedding)
                VALUES (?, ?, ?, ?::vector)
                """;

        jdbcTemplate.update(
                sql,
                documentId,
                chunkIndex,
                content,
                vector
        );
    }
}