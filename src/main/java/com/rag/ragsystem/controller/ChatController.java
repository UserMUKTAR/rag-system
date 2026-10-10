package com.rag.ragsystem.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rag.ragsystem.service.AnswerService;
import com.rag.ragsystem.service.SearchService;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final SearchService searchService;
    private final AnswerService answerService;

    public ChatController(
            SearchService searchService,
            AnswerService answerService) {

        this.searchService = searchService;
        this.answerService = answerService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> chat(
            @RequestBody Map<String, String> request) {

        String question = request.get("question");

        if (question == null || question.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Question must not be empty."));
        }

        // 1. Retrieve relevant passages from PostgreSQL
        List<String> context = searchService.search(question, 3);

        if (context.isEmpty()) {
            return ResponseEntity.ok(Map.of(
                    "question", question,
                    "answer", "No relevant passages were found in the uploaded documents.",
                    "sources", List.of()
            ));
        }

        // 2. Generate an answer using the retrieved passages
        String answer = answerService.generateAnswer(question, context);

        // 3. Return the answer and its supporting passages
        return ResponseEntity.ok(Map.of(
                "question", question,
                "answer", answer,
                "sources", context
        ));
    }
}