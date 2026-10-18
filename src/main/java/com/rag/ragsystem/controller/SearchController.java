package com.rag.ragsystem.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rag.ragsystem.exception.ApiErrorResponse;
import com.rag.ragsystem.service.SearchService;
import com.rag.ragsystem.service.SearchService.SearchResult;

import java.time.Instant;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping
    public ResponseEntity<?> search(
            @RequestParam(required = false) String question,
            @RequestParam(defaultValue = "3") int limit) {

        if (question == null || question.isBlank()) {
            return ResponseEntity.badRequest().body(
                    new ApiErrorResponse(
                            Instant.now(),
                            400,
                            "Bad Request",
                            "Question must not be empty.",
                            "/api/search"));
        }

        if (limit < 1 || limit > 10) {
            return ResponseEntity.badRequest().body(
                    new ApiErrorResponse(
                            Instant.now(),
                            400,
                            "Bad Request",
                            "Search limit must be between 1 and 10.",
                            "/api/search"));
        }

        List<SearchResult> results =
                searchService.search(question, limit);

        return ResponseEntity.ok(results);
    }
}