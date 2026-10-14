package com.rag.ragsystem.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class DocumentIngestionService {

    private final PdfService pdfService;
    private final TextChunker textChunker;
    private final EmbeddingService embeddingService;
    private final DocumentPersistenceService documentPersistenceService;

    public DocumentIngestionService(
            PdfService pdfService,
            TextChunker textChunker,
            EmbeddingService embeddingService,
            DocumentPersistenceService documentPersistenceService) {

        this.pdfService = pdfService;
        this.textChunker = textChunker;
        this.embeddingService = embeddingService;
        this.documentPersistenceService = documentPersistenceService;
    }

    public List<String> ingest(MultipartFile file) throws IOException {

        // Step 1: Extract text from the PDF.
        String extractedText = pdfService.extractText(file);

        if (extractedText == null || extractedText.isBlank()) {
            throw new IllegalArgumentException(
                    "The PDF contains no extractable text.");
        }

        // Step 2: Divide the text into chunks.
        List<String> chunks = textChunker.chunkText(extractedText);

        if (chunks.isEmpty()) {
            throw new IllegalArgumentException(
                    "No text chunks could be created from the PDF.");
        }

        // Step 3: Generate embeddings outside the database transaction.
        List<List<Double>> embeddings = new ArrayList<>();

        for (String chunk : chunks) {
            List<Double> embedding = embeddingService.generateEmbedding(chunk);
            embeddings.add(embedding);
        }

        // Step 4: Save the document and all chunks transactionally.
        documentPersistenceService.saveDocumentAndChunks(
                file.getOriginalFilename(),
                file.getContentType(),
                chunks,
                embeddings);

        // Step 5: Return the generated chunks.
        return chunks;
    }
}