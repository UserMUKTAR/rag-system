package com.rag.ragsystem.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rag.ragsystem.service.SearchService;
import com.rag.ragsystem.service.SearchService.SearchResult;

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
                    Map.of("error", "Question must not be empty."));
        }

        if (limit < 1 || limit > 10) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", "Search limit must be between 1 and 10."));
        }

        List<SearchResult> results =
                searchService.search(question, limit);

        return ResponseEntity.ok(results);
    }
}