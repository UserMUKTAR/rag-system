package com.rag.ragsystem.service;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class EmbeddingService {

    private final RestClient restClient;

    public EmbeddingService() {
        this.restClient = RestClient.builder()
                .baseUrl("http://localhost:11434")
                .build();
    }
    public List<Double> generateEmbedding(String text) {

        EmbeddingRequest request = new EmbeddingRequest(
                "nomic-embed-text",
                text
        );

        EmbeddingResponse response = restClient.post()
                .uri("/api/embed")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(EmbeddingResponse.class);

        return response.embeddings().get(0);
    }

    private record EmbeddingRequest(
            String model,
            String input
    ) {
    }

    private record EmbeddingResponse(
            List<List<Double>> embeddings
    ) {
    }
}