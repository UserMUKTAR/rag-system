package com.rag.ragsystem.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rag.ragsystem.service.EmbeddingService;

@RestController
public class EmbeddingController {

    private final EmbeddingService embeddingService;

    public EmbeddingController(EmbeddingService embeddingService) {
        this.embeddingService = embeddingService;
    }

    @GetMapping("/api/test-embedding")
    public List<Double> testEmbedding(
            @RequestParam String text) {

        return embeddingService.generateEmbedding(text);
    }
}