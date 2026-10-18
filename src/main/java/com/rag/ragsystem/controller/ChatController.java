package com.rag.ragsystem.controller;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rag.ragsystem.exception.ApiErrorResponse;
import com.rag.ragsystem.service.AnswerService;
import com.rag.ragsystem.service.SearchService;
import com.rag.ragsystem.service.SearchService.SearchResult;

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
    public ResponseEntity<?> chat(
            @RequestBody Map<String, String> request) {

        String question = request.get("question");

        if (question == null || question.isBlank()) {
            return ResponseEntity.badRequest().body(
                    new ApiErrorResponse(
                            Instant.now(),
                            400,
                            "Bad Request",
                            "Question must not be empty.",
                            "/api/chat"));
        }

        List<SearchResult> searchResults =
                searchService.search(question, 3);

        if (searchResults.isEmpty()) {
            return ResponseEntity.ok(Map.of(
                    "question", question,
                    "answer",
                    "No relevant passages were found in the uploaded documents.",
                    "sources", List.of()
            ));
        }

        // Extract passage text for the LLM.
        List<String> context = searchResults.stream()
                .map(SearchResult::content)
                .toList();

        String answer =
                answerService.generateAnswer(question, context);

        return ResponseEntity.ok(Map.of(
                "question", question,
                "answer", answer,
                "sources", searchResults
        ));
    }
}