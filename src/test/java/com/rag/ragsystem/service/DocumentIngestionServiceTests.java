package com.rag.ragsystem.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import com.rag.ragsystem.exception.DuplicateDocumentException;
import com.rag.ragsystem.repository.DocumentRepository;

@ExtendWith(MockitoExtension.class)
class DocumentIngestionServiceTests {

    @Mock
    private PdfService pdfService;

    @Mock
    private TextChunker textChunker;

    @Mock
    private EmbeddingService embeddingService;

    @Mock
    private DocumentPersistenceService documentPersistenceService;

    @Mock
    private DocumentRepository documentRepository;

    private DocumentIngestionService ingestionService;

    @BeforeEach
    void setUp() {
        ingestionService = new DocumentIngestionService(
                pdfService,
                textChunker,
                embeddingService,
                documentPersistenceService,
                documentRepository);
    }

    @Test
    void ingestShouldRejectDuplicatePdfBeforeExtractingText()
            throws Exception {

        byte[] pdfBytes = "sample-pdf-content"
                .getBytes(StandardCharsets.UTF_8);

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "guide.pdf",
                "application/pdf",
                pdfBytes);



        // Calculate the expected hash from the test bytes.
        String expectedHash = java.util.HexFormat.of().formatHex(
                java.security.MessageDigest.getInstance("SHA-256")
                        .digest(pdfBytes));

        when(documentRepository.existsBySha256Hash(expectedHash))
                .thenReturn(true);

        assertThrows(
                DuplicateDocumentException.class,
                () -> ingestionService.ingest(file));

        verify(pdfService, never()).extractText(file);
        verify(embeddingService, never()).generateEmbedding(
                org.mockito.ArgumentMatchers.anyString());
        verify(documentPersistenceService, never())
                .saveDocumentAndChunks(
                        org.mockito.ArgumentMatchers.anyString(),
                        org.mockito.ArgumentMatchers.any(),
                        org.mockito.ArgumentMatchers.anyString(),
                        org.mockito.ArgumentMatchers.anyList(),
                        org.mockito.ArgumentMatchers.anyList());
    }


    @Test
    void ingestShouldProcessNewPdfAndPersistChunks() throws Exception {

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "new-guide.pdf",
                "application/pdf",
                "new-pdf-content".getBytes(
                        java.nio.charset.StandardCharsets.UTF_8));

        String extractedText = "This is new document content.";
        List<String> chunks = List.of(extractedText);
        List<Double> embedding = java.util.Collections.nCopies(768, 0.1);
        List<List<Double>> embeddings = List.of(embedding);

        when(documentRepository.existsBySha256Hash(anyString()))
                .thenReturn(false);

        when(pdfService.extractText(file))
                .thenReturn(extractedText);

        when(textChunker.chunkText(extractedText))
                .thenReturn(chunks);

        when(embeddingService.generateEmbedding(extractedText))
                .thenReturn(embedding);

        List<String> result = ingestionService.ingest(file);

        assertEquals(chunks, result);

        verify(documentRepository).existsBySha256Hash(anyString());
        verify(pdfService).extractText(file);
        verify(textChunker).chunkText(extractedText);
        verify(embeddingService).generateEmbedding(extractedText);

        verify(documentPersistenceService).saveDocumentAndChunks(
                eq("new-guide.pdf"),
                eq("application/pdf"),
                anyString(),
                eq(chunks),
                eq(embeddings));
    }

    @Test
    void ingestShouldRejectEmptyFileBeforeCheckingDuplicate() {
        MockMultipartFile emptyFile = new MockMultipartFile(
                "file",
                "empty.pdf",
                "application/pdf",
                new byte[0]);

        assertThrows(
                IllegalArgumentException.class,
                () -> ingestionService.ingest(emptyFile));

        verifyNoInteractions(documentRepository);
        verifyNoInteractions(pdfService);
        verifyNoInteractions(embeddingService);
        verifyNoInteractions(documentPersistenceService);
    }


}
