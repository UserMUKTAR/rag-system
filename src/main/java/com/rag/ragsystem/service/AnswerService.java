package com.rag.ragsystem.service;

import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class AnswerService {

    private final RestClient restClient;

    public AnswerService() {
        this.restClient = RestClient.builder()
                .baseUrl("http://localhost:11434")
                .build();
    }

    public String generateAnswer(String question, List<String> context) {

        String combinedContext = String.join("\n\n---\n\n", context);

        String prompt = """
                You are a helpful assistant answering questions about a document.

                Use only the information in the CONTEXT below to answer the QUESTION.
                If the context does not contain enough information, say so clearly.
                Do not invent facts or claim that the document says something it does not.

                CONTEXT:
                %s

                QUESTION:
                %s

                ANSWER:
                """.formatted(combinedContext, question);

        Map<String, Object> request = Map.of(
                "model", "qwen2.5:3b",
                "prompt", prompt,
                "stream", false
        );

        Map<?, ?> response = restClient.post()
                .uri("/api/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(Map.class);

        if (response == null || response.get("response") == null) {
            throw new IllegalStateException(
                    "Ollama returned an empty answer.");
        }

        return response.get("response").toString().trim();
    }
}