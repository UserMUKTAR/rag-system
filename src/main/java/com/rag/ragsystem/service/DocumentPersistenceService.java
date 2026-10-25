package com.rag.ragsystem.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rag.ragsystem.entity.Document;
import com.rag.ragsystem.repository.DocumentRepository;

@Service
public class DocumentPersistenceService {

    private final DocumentRepository documentRepository;
    private final ChunkStorageService chunkStorageService;

    public DocumentPersistenceService(
            DocumentRepository documentRepository,
            ChunkStorageService chunkStorageService) {

        this.documentRepository = documentRepository;
        this.chunkStorageService = chunkStorageService;
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveDocumentAndChunks(
            String fileName,
            String fileType,
            String sha256Hash,
            List<String> chunks,
            List<List<Double>> embeddings) {

        if (chunks == null || chunks.isEmpty()) {
            throw new IllegalArgumentException("No text chunks to save.");
        }

        if (embeddings == null || embeddings.size() != chunks.size()) {
            throw new IllegalArgumentException(
                    "Each text chunk must have a corresponding embedding.");
        }

        // Save the document metadata.
        Document document = new Document();

        document.setFileName(fileName);
        document.setFileType(fileType);
        document.setSha256Hash(sha256Hash);

        Document savedDocument = documentRepository.save(document);

        // Save all chunks and their embeddings.
        for (int i = 0; i < chunks.size(); i++) {
            chunkStorageService.saveChunk(
                    savedDocument.getId(),
                    i,
                    chunks.get(i),
                    embeddings.get(i));
        }
    }
}
