package com.rag.ragsystem;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.rag.ragsystem.controller.SearchController;
import com.rag.ragsystem.exception.GlobalExceptionHandler;
import com.rag.ragsystem.service.SearchService;
import com.rag.ragsystem.service.SearchService.SearchResult;

class SearchControllerTests {

    private MockMvc mockMvc;
    private SearchService searchService;

    @BeforeEach
    void setUp() {
        searchService = org.mockito.Mockito.mock(SearchService.class);

        SearchController searchController = new SearchController(searchService);

        mockMvc = MockMvcBuilders.standaloneSetup(searchController).setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void missingQuestionShouldReturnBadRequest() throws Exception {

        mockMvc.perform(get("/api/search")).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400)).andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Question must not be empty."))
                .andExpect(jsonPath("$.path").value("/api/search"));

        verifyNoInteractions(searchService);
    }

    @Test
    void invalidLimitShouldReturnBadRequest() throws Exception {

        mockMvc.perform(get("/api/search").param("question", "What is RAG?").param("limit", "0"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Search limit must be between 1 and 10."));

        verifyNoInteractions(searchService);
    }

    @Test
    void limitAboveMaximumShouldReturnBadRequest() throws Exception {

        mockMvc.perform(get("/api/search").param("question", "What is RAG?").param("limit", "11"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Search limit must be between 1 and 10."));

        verifyNoInteractions(searchService);
    }

    @Test
    void nonNumericLimitShouldReturnBadRequest() throws Exception {

        mockMvc.perform(get("/api/search").param("question", "What is RAG?").param("limit", "abc"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value("Invalid request. Please check the supplied parameters or request body."));

        verifyNoInteractions(searchService);
    }

    @Test
    void validSearchShouldReturnResults() throws Exception {

        List<SearchResult> mockResults = List
                .of(new SearchResult("Retrieval-Augmented Generation combines retrieval with generation.", 0.20, 0.80));

        when(searchService.search("What is RAG?", 3)).thenReturn(mockResults);

        mockMvc.perform(get("/api/search").param("question", "What is RAG?").param("limit", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content")
                        .value("Retrieval-Augmented Generation combines retrieval with generation."))
                .andExpect(jsonPath("$[0].cosineDistance").value(0.20))
                .andExpect(jsonPath("$[0].cosineSimilarity").value(0.80));

        verify(searchService).search("What is RAG?", 3);
    }

    @Test
    void blankQuestionShouldReturnBadRequest() throws Exception {

        mockMvc.perform(get("/api/search").param("question", "   ")).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Question must not be empty."));

        verifyNoInteractions(searchService);
    }
}