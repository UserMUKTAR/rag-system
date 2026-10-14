package com.rag.ragsystem.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.rag.ragsystem.service.DocumentIngestionService;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final DocumentIngestionService documentIngestionService;

    public DocumentController(
            DocumentIngestionService documentIngestionService) {
        this.documentIngestionService = documentIngestionService;
    }

    @PostMapping("/upload")
    public ResponseEntity<List<String>> uploadPdf(
            @RequestParam("file") MultipartFile file) throws IOException {

        List<String> chunks = documentIngestionService.ingest(file);

        return ResponseEntity.ok(chunks);
    }
}