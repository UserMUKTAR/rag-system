package com.rag.ragsystem.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.rag.ragsystem.entity.Document;
import com.rag.ragsystem.repository.DocumentRepository;
import com.rag.ragsystem.service.ChunkStorageService;
import com.rag.ragsystem.service.EmbeddingService;
import com.rag.ragsystem.service.PdfService;
import com.rag.ragsystem.service.TextChunker;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final PdfService pdfService;
    private final TextChunker textChunker;
    private final DocumentRepository documentRepository;
    private final EmbeddingService embeddingService;
    private final ChunkStorageService chunkStorageService;

    public DocumentController(
            PdfService pdfService,
            TextChunker textChunker,
            DocumentRepository documentRepository,
            EmbeddingService embeddingService,
            ChunkStorageService chunkStorageService) {

        this.pdfService = pdfService;
        this.textChunker = textChunker;
        this.documentRepository = documentRepository;
        this.embeddingService = embeddingService;
        this.chunkStorageService = chunkStorageService;
    }

    @PostMapping("/upload")
    public ResponseEntity<List<String>> uploadPdf(
            @RequestParam("file") MultipartFile file) throws IOException {

        // 1. Extract text from the PDF
        String extractedText = pdfService.extractText(file);

        // 2. Save document metadata
        Document document = new Document();
        document.setFileName(file.getOriginalFilename());
        document.setFileType(file.getContentType());

        Document savedDocument = documentRepository.save(document);

        // 3. Split extracted text into chunks
        List<String> chunks = textChunker.chunkText(extractedText);

        // 4. Generate and save an embedding for each chunk
        for (int i = 0; i < chunks.size(); i++) {

            String chunk = chunks.get(i);

            List<Double> embedding =
                    embeddingService.generateEmbedding(chunk);

            chunkStorageService.saveChunk(
                    savedDocument.getId(),
                    i,
                    chunk,
                    embedding
            );
        }

        // 5. Return the processed chunks
        return ResponseEntity.ok(chunks);
    }
}