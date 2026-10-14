package com.rag.ragsystem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import com.rag.ragsystem.entity.Document;
import com.rag.ragsystem.repository.DocumentRepository;
import com.rag.ragsystem.service.ChunkStorageService;

@SpringBootTest
class RagSystemApplicationTests {

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private ChunkStorageService chunkStorageService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Test
    void contextLoads() {
    }

    @Test
    void documentAndChunksShouldRollbackWhenPersistenceFails() {

        long documentsBefore = documentRepository.count();

        Long[] testDocumentId = new Long[1];

        TransactionTemplate transactionTemplate =
                new TransactionTemplate(transactionManager);

        assertThrows(IllegalArgumentException.class, () ->
            transactionTemplate.executeWithoutResult(status -> {

                // 1. Insert document metadata.
                Document document = new Document();
                document.setFileName("rollback-test.pdf");
                document.setFileType("application/pdf");

                Document savedDocument =
                        documentRepository.saveAndFlush(document);

                testDocumentId[0] = savedDocument.getId();

                // 2. Insert one valid chunk with a 768-dimensional vector.
                List<Double> validEmbedding =
                        java.util.Collections.nCopies(768, 0.1);

                chunkStorageService.saveChunk(
                        savedDocument.getId(),
                        0,
                        "Temporary rollback test chunk",
                        validEmbedding);

                // 3. Confirm the chunk was inserted within the transaction.
                Integer chunksInsideTransaction = jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM document_chunks WHERE document_id = ?",
                        Integer.class,
                        savedDocument.getId());

                assertEquals(1, chunksInsideTransaction);

                // 4. Deliberately trigger the existing dimension validation.
                chunkStorageService.saveChunk(
                        savedDocument.getId(),
                        1,
                        "This chunk should never be inserted",
                        List.of(0.1, 0.2));
            })
        );

        // 5. Verify the document insert was rolled back.
        assertEquals(
                documentsBefore,
                documentRepository.count());

        // 6. Verify no chunks remain for the test document.
        Integer remainingChunks = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM document_chunks WHERE document_id = ?",
                Integer.class,
                testDocumentId[0]);

        assertEquals(0, remainingChunks);
    }
}