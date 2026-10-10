# RAG System — Project Tasks

## Project Goal

Build a local, free, production-style PDF Retrieval-Augmented
Generation (RAG) system using Java 17, Spring Boot, PostgreSQL
with pgvector, Ollama, and open-source models.

The system should ingest PDF documents, extract and chunk text,
generate embeddings, retrieve relevant passages, and generate
answers grounded in the retrieved context.

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

---

## Phase 3 — Retrieval

- [x] Generate query embeddings
- [x] Implement pgvector cosine-distance search
- [x] Retrieve top-K relevant chunks
- [x] Create `SearchService`
- [x] Create `SearchController`
- [x] Test semantic search using Postman
- [ ] Return similarity scores with retrieved chunks
- [ ] Evaluate retrieval relevance
- [ ] Tune chunk size and overlap
- [ ] Add document-specific retrieval
- [ ] Evaluate and configure vector indexing

---

## Phase 4 — RAG Generation

- [x] Select local LLM: Qwen2.5 3B
- [x] Install and test local LLM using Ollama
- [x] Create `AnswerService`
- [x] Create initial RAG prompt
- [x] Send retrieved context to the LLM
- [x] Generate answers from retrieved context
- [x] Create `ChatController`
- [x] Return answers with supporting passages
- [x] Test the end-to-end RAG API
- [x] Test an unsupported question
- [ ] Improve grounding and unsupported-question handling
- [ ] Evaluate answer quality with additional questions

---

## Phase 5 — API and Quality

- [x] Implement PDF upload REST API
- [x] Implement embedding test endpoint
- [x] Implement semantic search REST API
- [x] Implement question-answering REST API
- [ ] Add request validation and search-limit constraints
- [ ] Add consistent exception handling
- [ ] Add application logging
- [ ] Add transaction handling for document ingestion
- [ ] Prevent duplicate document uploads
- [ ] Add unit tests
- [ ] Add integration tests
- [ ] Improve API response structure
- [ ] Document API endpoints and example requests
- [ ] Review and improve project structure

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
- [ ] Prepare production-ready README
- [ ] Document architecture and deployment
- [ ] Perform final end-to-end verification

---

## Git and Version Control

- [x] Initialize Git repository
- [x] Create `main` branch
- [x] Connect GitHub remote
- [x] Push initial project to GitHub
- [x] Commit PostgreSQL integration checkpoint
- [ ] Commit and push the completed end-to-end RAG milestone
- [ ] Keep the working tree clean after each milestone

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

**Current Milestone**

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