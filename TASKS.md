# RAG System — Project Tasks

## Project Goal

Build a local, free, production-style PDF Retrieval-Augmented Generation (RAG) system using Java 17, Spring Boot, PostgreSQL with pgvector, Ollama, and open-source models.

The system should ingest PDF documents, extract and chunk text, generate embeddings, retrieve relevant passages, and generate answers grounded in the retrieved context.

---

## Phase 1 — Foundation

- [x] Create Spring Boot project
- [x] Configure Java 17
- [x] Add Apache PDFBox
- [x] Implement PDF upload endpoint
- [x] Extract text from PDF
- [x] Implement text chunking with overlap
- [x] Install Ollama
- [x] Install `nomic-embed-text`
- [x] Integrate Spring Boot with Ollama embeddings
- [x] Install PostgreSQL 16
- [x] Install pgvector 0.8.7
- [x] Create `ragdb` database
- [x] Enable the `vector` extension

---

## Phase 2 — Vector Database and Persistence

- [x] Design database schema
- [x] Create `documents` table
- [x] Create `document_chunks` table
- [x] Add `vector(768)` embedding column
- [x] Configure Spring Boot database connection
- [x] Create `Document` JPA entity
- [x] Create `DocumentChunk` JPA entity
- [x] Create `DocumentRepository`
- [x] Create `DocumentChunkRepository`
- [x] Implement JDBC-based chunk and embedding storage
- [x] Store document metadata in PostgreSQL
- [x] Store chunks and embeddings in PostgreSQL
- [x] Verify stored vector dimensions
- [x] Verify chunk-to-document relationships
- [x] Verify all 191 chunks have embeddings for document ID 3

---

## Phase 3 — Retrieval

- [x] Generate query embeddings
- [x] Implement pgvector cosine-distance search
- [x] Retrieve top-K chunks by cosine distance
- [x] Create `SearchService`
- [x] Create `SearchController`
- [x] Test semantic search using Postman
- [x] Return cosine distance and similarity scores
- [x] Implement provisional relevance threshold (0.50)
- [x] Test retrieval with relevant questions
- [x] Test rejection of unrelated search results
- [ ] Evaluate retrieval relevance across a broader question set
- [ ] Tune chunk size and overlap
- [ ] Improve retrieval by searching additional candidates before filtering
- [ ] Add document-specific retrieval
- [ ] Evaluate and configure vector indexing
- [ ] Evaluate and finalize the relevance threshold

---

## Phase 4 — RAG Generation

- [x] Select local LLM: Qwen2.5 3B
- [x] Install and test local LLM using Ollama
- [x] Create `AnswerService`
- [x] Create initial RAG prompt
- [x] Improve prompt grounding instructions
- [x] Send retrieved context to the LLM
- [x] Generate context-based answers
- [x] Create `ChatController`
- [x] Return answers with supporting passages
- [x] Test the end-to-end RAG API
- [x] Test an unsupported question
- [x] Handle questions when no relevant passages are found
- [ ] Evaluate answer grounding across a broader question set
- [ ] Improve answers when context contains partial information
- [ ] Evaluate answer accuracy and source attribution
- [ ] Reduce unsupported claims in generated answers

---

## Phase 5 — API and Quality

### Completed

- [x] Implement PDF upload REST API
- [x] Implement embedding test endpoint
- [x] Implement semantic search REST API
- [x] Implement question-answering REST API
- [x] Validate missing and blank questions in Search API
- [x] Validate search-limit constraints (1–10)
- [x] Validate missing and blank questions in Chat API
- [x] Test valid and invalid search requests
- [x] Test valid and invalid chat requests
- [x] Return meaningful validation error messages
- [x] Validate PDF text before persistence
- [x] Validate that text chunks can be created
- [x] Implement transactional document and chunk persistence
- [x] Generate embeddings outside the database transaction
- [x] Verify rollback of document metadata and chunk inserts in a controlled integration test
- [x] Verify successful PDF upload after ingestion changes
- [x] Verify stored chunks and embeddings after PDF ingestion
- [x] Run the Spring application successfully after ingestion changes
- [x] Run the existing JUnit tests successfully
- [x] Handle non-numeric search limits with consistent error responses
- [x] Add centralized exception handling
- [x] Handle empty and invalid uploaded files consistently
- [x] Return structured error responses for ingestion failures
- [x] Add unit tests for global exception handling (HTTP 400 and HTTP 500)

### Remaining

- [ ] Add application logging
- [ ] Prevent duplicate document uploads
- [ ] Test ingestion when Ollama is unavailable
- [ ] Test ingestion when embedding generation fails partway through a document
- [ ] Verify rollback for additional database failure scenarios
- [ ] Expand integration test coverage
- [ ] Improve API response structure
- [ ] Document API endpoints and example requests
- [ ] Review and improve project structure
- [ ] Investigate Unicode text extraction and display issues

---

## Phase 6 — Professionalization

- [x] Use environment variable for database password
- [ ] Review environment-based configuration
- [ ] Add a health-check endpoint
- [ ] Dockerize the Spring Boot application
- [ ] Dockerize PostgreSQL with pgvector
- [ ] Add application health checks
- [ ] Add monitoring
- [ ] Add CI/CD
- [ ] Review application security
- [ ] Improve configuration and secret management
- [ ] Add configurable Ollama connection settings
- [ ] Add external-service timeouts and error handling
- [ ] Review database transaction duration and connection usage
- [ ] Evaluate memory usage when ingesting large PDFs
- [ ] Prepare production-ready README
- [ ] Document architecture and deployment
- [ ] Perform final end-to-end verification

---

## Git and Version Control

### Previously Completed

- [x] Initialize Git repository
- [x] Create `main` branch
- [x] Connect GitHub remote
- [x] Push initial project to GitHub
- [x] Commit PostgreSQL integration checkpoint
- [x] Implement and push the basic end-to-end RAG pipeline
- [x] Commit and push retrieval improvements
- [x] Commit and push API validation improvements
- [x] Commit and push prompt improvements
- [x] Commit and push the updated task checklist
- [x] Verify the previous checkpoint was synchronized with GitHub

### Current Ingestion Reliability Changes

- [x] Review the current Git diff
- [x] Verify the final code and test changes
- [x] Commit ingestion reliability changes
- [x] Push ingestion reliability changes to GitHub
- [x] Verify the working tree is clean after committing
- [x] Verify the latest changes are available on GitHub

---

## Daily Progress

### 2026-10-08

**Completed**

- Spring Boot project created
- PDF upload, extraction, and chunking implemented
- Ollama and embedding model configured
- PostgreSQL configured
- pgvector 0.8.7 installed
- `ragdb` created
- Vector extension enabled
- Git repository initialized

**Milestone**

Foundation and initial database setup completed.

---

### 2026-10-09

**Completed**

- PostgreSQL schema created
- `documents` table created
- `document_chunks` table created
- `vector(768)` column configured
- Spring Boot database connection established
- Database integration checkpoint committed and pushed

**Milestone**

PostgreSQL and pgvector integration completed.

---

### 2026-10-10

**Completed**

- Created `Document` and `DocumentChunk` entities
- Created JPA repositories
- Implemented document metadata persistence
- Implemented chunk and embedding storage using JDBC
- Verified 191 chunks for the test document
- Verified all 191 chunks have 768-dimensional embeddings
- Implemented semantic search using pgvector
- Created `SearchService` and `SearchController`
- Installed and tested Qwen2.5 3B locally
- Created `AnswerService` and `ChatController`
- Implemented end-to-end retrieval and answer generation
- Verified the chat API returns an answer and supporting passages
- Tested a question not answered by the uploaded document

**Milestone**

Basic end-to-end local RAG pipeline implemented and tested.

**Current Capabilities**

- Upload PDF documents
- Extract and chunk PDF text
- Generate embeddings locally
- Store document metadata, chunks, and vectors
- Search for semantically similar passages
- Generate answers using a local language model
- Return answers with supporting passages

**Known Improvements Needed**

- Evaluate retrieval relevance and similarity scores
- Improve handling of unsupported questions
- Add transaction handling and duplicate-upload protection
- Add automated tests and robust exception handling

**Next Milestone**

Improve retrieval quality and ingestion reliability.

---

### 2026-10-12

**Completed**

- Added cosine-distance calculation to semantic search
- Returned cosine distance and similarity scores with retrieved passages
- Implemented provisional cosine similarity threshold of 0.50
- Tested retrieval with relevant questions
- Tested rejection of unrelated search results
- Verified the Chat API fallback when no relevant passages are found
- Added Search API validation for missing and blank questions
- Added Search API search-limit validation (1–10)
- Tested valid and invalid Search API requests
- Verified Chat API validation for missing and blank questions
- Tested valid and invalid Chat API requests
- Improved the RAG prompt with stronger grounding instructions
- Retested answer generation and unsupported-question handling
- Committed and pushed retrieval, API validation, and prompt improvements
- Updated and pushed the project task checklist

**Milestone**

Retrieval quality improvements and API validation implemented.

**Current Capabilities**

- Upload PDF documents
- Extract and chunk PDF text
- Generate embeddings locally
- Store document metadata, chunks, and vectors
- Retrieve passages using pgvector cosine-distance search
- Return cosine distance and similarity scores
- Filter results using a provisional relevance threshold
- Generate answers using the local Qwen2.5 3B model
- Return answers with supporting passages
- Reject questions when no retrieved passages meet the threshold
- Validate Search API and Chat API questions
- Enforce search-limit constraints

**Known Improvements Needed**

- Evaluate retrieval quality across a broader question set
- Validate and tune the relevance threshold
- Improve retrieval candidate selection
- Improve answer grounding and partial-context handling
- Add document-specific retrieval
- Add duplicate-upload protection
- Add centralized exception handling and application logging
- Expand unit and integration test coverage
- Complete ingestion reliability improvements

**Next Milestone**

Complete ingestion reliability improvements and establish a verified Git checkpoint.

---

### 2026-10-13

**Completed**

- Created `DocumentIngestionService` to coordinate PDF ingestion
- Added validation for PDFs containing no extractable text
- Added validation for empty chunk collections
- Created `DocumentPersistenceService`
- Moved embedding generation outside the database transaction
- Implemented transactional persistence for document metadata and chunks
- Verified successful application startup after the changes
- Uploaded a PDF successfully through Postman and received HTTP 200 OK
- Verified document ID 3 contains 191 chunks
- Verified all 191 chunks have embeddings
- Recorded the database baseline before rollback testing: 3 documents and 382 chunks
- Added a controlled transaction rollback integration test
- Verified the document insert was rolled back after a deliberate validation failure
- Verified no chunks remained for the temporary test document
- Confirmed both JUnit tests passed

**Milestone**

Document ingestion reliability and transactional persistence implemented and tested.

**Current Capabilities**

- Upload PDF documents through the REST API
- Extract text from PDFs
- Reject PDFs with no extractable text
- Split extracted text into overlapping chunks
- Generate embeddings locally using Ollama
- Store document metadata in PostgreSQL
- Store text chunks and vector embeddings using JDBC
- Persist document metadata and chunks transactionally
- Roll back document and chunk writes in the tested failure scenario
- Retrieve relevant passages using pgvector
- Generate answers using the local Qwen2.5 3B model
- Return answers with supporting passages
- Validate Search API and Chat API inputs

**Known Improvements Needed**

- Evaluate retrieval quality across a broader question set
- Validate and tune the provisional relevance threshold
- Improve retrieval candidate selection
- Add document-specific retrieval
- Improve answer grounding and partial-context handling
- Prevent duplicate document uploads
- Add centralized exception handling
- Add application logging
- Expand unit and integration test coverage
- Test additional PDF ingestion failure scenarios
- Investigate Unicode text extraction and display issues

**Next Milestone**

Continue improving retrieval quality and API robustness.

---

### 2026-10-14

**Completed**

- Committed ingestion reliability changes as `c21f587` (`feat: improve document ingestion reliability`)
- Pushed the commit to GitHub on `main`
- Verified local `main` is up to date with `origin/main`
- Verified the working tree is clean

**Milestone**

Ingestion reliability changes committed, pushed, and synchronized with GitHub.

**Next Milestone**

Continue improving API robustness, then evaluate retrieval quality across a broader question set.

---

### 2026-10-18

- Implemented centralized API exception handling with structured error responses.
- Improved validation for search limits, chat requests, and PDF uploads.
- Verified valid PDF uploads and invalid or missing upload scenarios.
- Ran `mvn test`: 2 tests passed, 0 failures, 0 errors.

---

### 2026-10-21

- Added GlobalExceptionHandlerTests for structured HTTP 400 and HTTP 500 responses.
- Ran `mvn test` successfully: 4 tests passed, 0 failures, 0 errors.

---