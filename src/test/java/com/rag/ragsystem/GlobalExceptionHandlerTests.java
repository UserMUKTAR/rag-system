package com.rag.ragsystem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.http.ResponseEntity;

import com.rag.ragsystem.exception.ApiErrorResponse;
import com.rag.ragsystem.exception.GlobalExceptionHandler;

class GlobalExceptionHandlerTests {

    private GlobalExceptionHandler exceptionHandler;
    private MockHttpServletRequest request;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();

        request = new MockHttpServletRequest();
        request.setRequestURI("/api/test");
    }

    @Test
    void illegalArgumentShouldReturnBadRequest() {
        IllegalArgumentException exception =
                new IllegalArgumentException("Question must not be empty.");

        ResponseEntity<ApiErrorResponse> response =
                exceptionHandler.handleIllegalArgument(exception, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().status());
        assertEquals("Bad Request", response.getBody().error());
        assertEquals(
                "Question must not be empty.",
                response.getBody().message());
        assertEquals("/api/test", response.getBody().path());
        assertNotNull(response.getBody().timestamp());
    }

    @Test
    void unexpectedExceptionShouldReturnInternalServerError() {
        RuntimeException exception =
                new RuntimeException("Internal test exception");

        ResponseEntity<ApiErrorResponse> response =
                exceptionHandler.handleUnexpectedException(exception, request);

        assertEquals(
                HttpStatus.INTERNAL_SERVER_ERROR,
                response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(500, response.getBody().status());
        assertEquals("Internal Server Error", response.getBody().error());
        assertEquals(
                "An unexpected server error occurred.",
                response.getBody().message());
        assertEquals("/api/test", response.getBody().path());
        assertNotNull(response.getBody().timestamp());
    }
}