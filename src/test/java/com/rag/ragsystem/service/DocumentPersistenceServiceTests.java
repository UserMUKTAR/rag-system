package com.rag.ragsystem.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.postgresql.util.PSQLException;
import org.postgresql.util.ServerErrorMessage;
import org.springframework.dao.DataIntegrityViolationException;

import com.rag.ragsystem.entity.Document;
import com.rag.ragsystem.exception.DuplicateDocumentException;
import com.rag.ragsystem.repository.DocumentRepository;

@ExtendWith(MockitoExtension.class)
class DocumentPersistenceServiceTests {

	@Mock
	private DocumentRepository documentRepository;

	@Mock
	private ChunkStorageService chunkStorageService;

	private DocumentPersistenceService documentPersistenceService;

	@BeforeEach
	void setUp() {
		documentPersistenceService = new DocumentPersistenceService(documentRepository, chunkStorageService);
	}

	@Test
	void shouldTranslateDuplicateHashConstraintViolation() {
		PSQLException postgresException = mock(PSQLException.class);
		ServerErrorMessage serverErrorMessage = mock(ServerErrorMessage.class);

		when(postgresException.getServerErrorMessage()).thenReturn(serverErrorMessage);

		when(serverErrorMessage.getConstraint()).thenReturn("uq_documents_sha256_hash");

		DataIntegrityViolationException integrityException = new DataIntegrityViolationException(
				"Database constraint violation", postgresException);

		when(documentRepository.saveAndFlush(any(Document.class))).thenThrow(integrityException);

		assertThrows(DuplicateDocumentException.class,
				() -> documentPersistenceService.saveDocumentAndChunks("guide.pdf", "application/pdf", "abc123",
						List.of("Sample text"), List.of(List.of(0.1))));

		verify(documentRepository).saveAndFlush(any(Document.class));

		verify(chunkStorageService, never()).saveChunk(org.mockito.ArgumentMatchers.anyLong(),
				org.mockito.ArgumentMatchers.anyInt(), org.mockito.ArgumentMatchers.anyString(),
				org.mockito.ArgumentMatchers.anyList());
	}

	@Test
	void shouldRethrowUnrelatedIntegrityViolation() {
		DataIntegrityViolationException originalException = new DataIntegrityViolationException(
				"Some unrelated database constraint was violated.");

		when(documentRepository.saveAndFlush(any(Document.class))).thenThrow(originalException);

		DataIntegrityViolationException thrown = assertThrows(DataIntegrityViolationException.class,
				() -> documentPersistenceService.saveDocumentAndChunks("guide.pdf", "application/pdf", "abc123",
						List.of("Sample text"), List.of(List.of(0.1))));

		org.junit.jupiter.api.Assertions.assertSame(originalException, thrown);

		verify(chunkStorageService, never()).saveChunk(org.mockito.ArgumentMatchers.anyLong(),
				org.mockito.ArgumentMatchers.anyInt(), org.mockito.ArgumentMatchers.anyString(),
				org.mockito.ArgumentMatchers.anyList());
	}
}