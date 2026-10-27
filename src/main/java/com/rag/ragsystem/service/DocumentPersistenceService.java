package com.rag.ragsystem.service;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.postgresql.util.PSQLException;

import com.rag.ragsystem.entity.Document;
import com.rag.ragsystem.exception.DuplicateDocumentException;
import com.rag.ragsystem.repository.DocumentRepository;

@Service
public class DocumentPersistenceService {

	private final DocumentRepository documentRepository;
	private final ChunkStorageService chunkStorageService;

	public DocumentPersistenceService(DocumentRepository documentRepository, ChunkStorageService chunkStorageService) {

		this.documentRepository = documentRepository;
		this.chunkStorageService = chunkStorageService;
	}

	@Transactional(rollbackFor = Exception.class)
	public void saveDocumentAndChunks(String fileName, String fileType, String sha256Hash, List<String> chunks,
			List<List<Double>> embeddings) {

		if (chunks == null || chunks.isEmpty()) {
			throw new IllegalArgumentException("No text chunks to save.");
		}

		if (embeddings == null || embeddings.size() != chunks.size()) {
			throw new IllegalArgumentException("Each text chunk must have a corresponding embedding.");
		}

		// Save the document metadata.
		Document document = new Document();

		document.setFileName(fileName);
		document.setFileType(fileType);
		document.setSha256Hash(sha256Hash);

		Document savedDocument;

		try {
			savedDocument = documentRepository.saveAndFlush(document);
		} catch (DataIntegrityViolationException ex) {
			if (isDuplicateHashViolation(ex)) {
				throw new DuplicateDocumentException("This PDF has already been uploaded.");
			}

			throw ex;
		}

		// Save all chunks and their embeddings.
		for (int i = 0; i < chunks.size(); i++) {
			chunkStorageService.saveChunk(savedDocument.getId(), i, chunks.get(i), embeddings.get(i));
		}
	}

	private boolean isDuplicateHashViolation(Throwable ex) {
		Throwable current = ex;

		while (current != null) {
			if (current instanceof PSQLException postgresException) {
				if (postgresException.getServerErrorMessage() != null && "uq_documents_sha256_hash"
						.equals(postgresException.getServerErrorMessage().getConstraint())) {
					return true;
				}
			}

			current = current.getCause();
		}

		return false;
	}

}
