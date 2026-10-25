package com.rag.ragsystem.service;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.rag.ragsystem.exception.DuplicateDocumentException;
import com.rag.ragsystem.repository.DocumentRepository;

@Service
public class DocumentIngestionService {

	private final PdfService pdfService;
	private final TextChunker textChunker;
	private final EmbeddingService embeddingService;
	private final DocumentPersistenceService documentPersistenceService;
	private final DocumentRepository documentRepository;

	public DocumentIngestionService(PdfService pdfService, TextChunker textChunker, EmbeddingService embeddingService,
			DocumentPersistenceService documentPersistenceService, DocumentRepository documentRepository) {

		this.pdfService = pdfService;
		this.textChunker = textChunker;
		this.embeddingService = embeddingService;
		this.documentPersistenceService = documentPersistenceService;
		this.documentRepository = documentRepository;
	}

	public List<String> ingest(MultipartFile file) throws IOException {

		if (file == null || file.isEmpty()) {
			throw new IllegalArgumentException("Uploaded file must not be empty.");
		}

		String sha256Hash = calculateSha256(file);

		if (documentRepository.existsBySha256Hash(sha256Hash)) {
			throw new DuplicateDocumentException("This PDF has already been uploaded.");
		}

		// Step 1: Extract text from the PDF.
		String extractedText = pdfService.extractText(file);

		if (extractedText == null || extractedText.isBlank()) {
			throw new IllegalArgumentException("The PDF contains no extractable text.");
		}

		// Step 2: Divide the text into chunks.
		List<String> chunks = textChunker.chunkText(extractedText);

		if (chunks.isEmpty()) {
			throw new IllegalArgumentException("No text chunks could be created from the PDF.");
		}

		// Step 3: Generate embeddings outside the database transaction.
		List<List<Double>> embeddings = new ArrayList<>();

		for (String chunk : chunks) {
			List<Double> embedding = embeddingService.generateEmbedding(chunk);
			embeddings.add(embedding);
		}

		// Step 4: Save the document and all chunks transactionally.
		documentPersistenceService.saveDocumentAndChunks(file.getOriginalFilename(), file.getContentType(), sha256Hash,
				chunks, embeddings);

		// Step 5: Return the generated chunks.
		return chunks;
	}

	private String calculateSha256(MultipartFile file) throws IOException {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(file.getBytes());
			return HexFormat.of().formatHex(hash);
		} catch (NoSuchAlgorithmException ex) {
			throw new IllegalStateException("SHA-256 algorithm is unavailable.", ex);
		}
	}
}
