package com.rag.ragsystem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.util.HexFormat;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;

import com.rag.ragsystem.service.EmbeddingService;
import com.rag.ragsystem.service.PdfService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "spring.servlet.multipart.max-file-size=2MB",
        "spring.servlet.multipart.max-request-size=2MB"
})
class ConcurrentDocumentUploadIntegrationTests {

    private static final String PDF_TEXT =
            "Concurrent duplicate upload integration test document. "
            + "This content is deliberately short and unique.";

    @LocalServerPort
    private int port;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private TestPdfService testPdfService;

    @Test
    void simultaneousUploadsOfSamePdfShouldCreateOnlyOneDocument() throws Exception {
        byte[] pdfBytes = createTestPdf();
        String hash = sha256(pdfBytes);

        Integer existingCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM documents WHERE sha256_hash = ?",
                Integer.class,
                hash);

        assertEquals(0, existingCount,
                "The test PDF hash must not already exist in the database.");

        testPdfService.prepareForConcurrentRequests();

        ExecutorService executor = Executors.newFixedThreadPool(2);

        try {
            Future<HttpResponse<String>> firstRequest =
                    executor.submit(() -> uploadPdf(pdfBytes));

            Future<HttpResponse<String>> secondRequest =
                    executor.submit(() -> uploadPdf(pdfBytes));

            assertTrue(
                    testPdfService.awaitBothRequests(Duration.ofSeconds(15)),
                    "Both requests should reach PDF extraction.");

            testPdfService.releaseRequests();

            HttpResponse<String> firstResponse =
                    firstRequest.get(60, TimeUnit.SECONDS);

            HttpResponse<String> secondResponse =
                    secondRequest.get(60, TimeUnit.SECONDS);

            List<Integer> statuses = List.of(
                    firstResponse.statusCode(),
                    secondResponse.statusCode());

            assertEquals(1, statuses.stream()
                    .filter(status -> status == 200)
                    .count(),
                    "Exactly one upload should succeed.");

            assertEquals(1, statuses.stream()
                    .filter(status -> status == 409)
                    .count(),
                    "Exactly one upload should return HTTP 409.");

            Integer documentCount = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM documents WHERE sha256_hash = ?",
                    Integer.class,
                    hash);

            assertEquals(1, documentCount,
                    "Only one document record should exist for the test PDF.");

            Integer chunkCount = jdbcTemplate.queryForObject(
                    """
                    SELECT COUNT(*)
                    FROM document_chunks c
                    JOIN documents d ON d.id = c.document_id
                    WHERE d.sha256_hash = ?
                    """,
                    Integer.class,
                    hash);

            assertTrue(chunkCount > 0,
                    "The successfully uploaded document should have chunks.");

        } finally {
            testPdfService.releaseRequests();
            executor.shutdownNow();
        }
    }

    private HttpResponse<String> uploadPdf(byte[] pdfBytes)
            throws IOException, InterruptedException {

        String boundary = "----RagTestBoundary123456";

        ByteArrayOutputStream body = new ByteArrayOutputStream();

        body.write(("--" + boundary + "\r\n"
                + "Content-Disposition: form-data; name=\"file\"; "
                + "filename=\"concurrent-test.pdf\"\r\n"
                + "Content-Type: application/pdf\r\n\r\n")
                .getBytes(StandardCharsets.UTF_8));

        body.write(pdfBytes);

        body.write(("\r\n--" + boundary + "--\r\n")
                .getBytes(StandardCharsets.UTF_8));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(
                        "http://localhost:" + port + "/api/documents/upload"))
                .timeout(Duration.ofSeconds(60))
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .POST(HttpRequest.BodyPublishers.ofByteArray(body.toByteArray()))
                .build();

        return HttpClient.newHttpClient().send(
                request,
                HttpResponse.BodyHandlers.ofString());
    }

    private byte[] createTestPdf() throws IOException {
        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream output = new ByteArrayOutputStream()) {

            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream content =
                    new PDPageContentStream(document, page)) {

                content.beginText();
                content.setFont(
                        new PDType1Font(Standard14Fonts.FontName.HELVETICA),
                        12
                );                content.newLineAtOffset(50, 700);
                content.showText(PDF_TEXT);
                content.endText();
            }

            document.save(output);
            return output.toByteArray();
        }
    }

    private String sha256(byte[] bytes) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        return HexFormat.of().formatHex(digest.digest(bytes));
    }

    @TestConfiguration
    static class ConcurrentUploadTestConfiguration {

        @Bean
        @Primary
        TestPdfService testPdfService() {
            return new TestPdfService();
        }

        @Bean
        @Primary
        TestEmbeddingService testEmbeddingService() {
            return new TestEmbeddingService();
        }
    }

    static class TestPdfService extends PdfService {

        private volatile CountDownLatch requestsReachedExtraction =
                new CountDownLatch(0);

        private volatile CountDownLatch releaseExtraction =
                new CountDownLatch(0);

        void prepareForConcurrentRequests() {
            requestsReachedExtraction = new CountDownLatch(2);
            releaseExtraction = new CountDownLatch(1);
        }

        boolean awaitBothRequests(Duration timeout)
                throws InterruptedException {
            return requestsReachedExtraction.await(
                    timeout.toMillis(), TimeUnit.MILLISECONDS);
        }

        void releaseRequests() {
            releaseExtraction.countDown();
        }

        @Override
        public String extractText(
                org.springframework.web.multipart.MultipartFile file) {

            requestsReachedExtraction.countDown();

            try {
                if (!releaseExtraction.await(20, TimeUnit.SECONDS)) {
                    throw new IllegalStateException(
                            "Timed out waiting to release PDF extraction.");
                }
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException(
                        "PDF extraction was interrupted.", ex);
            }

            return PDF_TEXT;
        }
    }

    static class TestEmbeddingService extends EmbeddingService {

        @Override
        public List<Double> generateEmbedding(String text) {
            return java.util.Collections.nCopies(768, 0.01);
        }
    }
}