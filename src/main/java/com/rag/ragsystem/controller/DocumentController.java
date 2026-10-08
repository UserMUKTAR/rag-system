package com.rag.ragsystem.controller;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.rag.ragsystem.service.PdfService;

import java.util.List;

import com.rag.ragsystem.service.TextChunker;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final PdfService pdfService;
    private final TextChunker textChunker;

    public DocumentController(PdfService pdfService, TextChunker textChunker) {
        this.pdfService = pdfService;
        this.textChunker = textChunker;
    }

    @PostMapping("/upload")
    public ResponseEntity<List<String>> uploadPdf(
            @RequestParam("file") MultipartFile file) throws IOException {

    	String extractedText = pdfService.extractText(file);

    	List<String> chunks = textChunker.chunkText(extractedText);

    	return ResponseEntity.ok(chunks);
    }
}