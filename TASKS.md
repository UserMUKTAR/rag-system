# RAG System — Project Tasks

## Project Goal

Build a local, free, production-style PDF RAG system using Java,
Spring Boot, PostgreSQL + pgvector, Ollama, and open-source models.

---

## Phase 1 — Foundation

- [x] Create Spring Boot project
- [x] Configure Java 17
- [x] Add Apache PDFBox
- [x] Implement PDF upload
- [x] Extract text from PDF
- [x] Implement text chunking
- [x] Install Ollama
- [x] Install `nomic-embed-text`
- [x] Integrate Spring Boot with Ollama
- [x] Install PostgreSQL 16
- [x] Install pgvector 0.8.7
- [x] Create `ragdb` database
- [x] Enable `vector` extension

---

## Phase 2 — Vector Database

- [x] Design database schema
- [x] Create documents table
- [x] Create document chunks table
- [x] Add vector column
- [ ] Configure Spring Boot database connection
- [ ] Create JPA/JDBC data access layer
- [ ] Store document metadata
- [ ] Store chunks and embeddings
- [ ] Verify vector data in PostgreSQL

---

## Phase 3 — Retrieval

- [ ] Generate query embeddings
- [ ] Implement vector similarity search
- [ ] Retrieve top-K relevant chunks
- [ ] Test semantic search
- [ ] Tune chunk size and overlap

---

## Phase 4 — RAG Generation

- [ ] Select local LLM
- [ ] Install/configure local LLM
- [ ] Create RAG prompt
- [ ] Send retrieved context to LLM
- [ ] Generate grounded answers
- [ ] Return answer with source information

---

## Phase 5 — API & Quality

- [ ] Create clean REST APIs
- [ ] Add validation
- [ ] Add exception handling
- [ ] Add logging
- [ ] Add unit tests
- [ ] Add integration tests
- [ ] Improve project structure
- [ ] Document API endpoints

---

## Phase 6 — Professionalization

- [ ] Environment-based configuration
- [ ] Dockerize application
- [ ] Dockerize PostgreSQL/pgvector
- [ ] Add health checks
- [ ] Add monitoring
- [ ] Add CI/CD
- [ ] Improve security
- [ ] Prepare production README

---

## Daily Progress

### 2026-10-08

**Completed**

- Spring Boot project created
- PDF ingestion pipeline implemented
- Ollama + embedding model configured
- PostgreSQL configured
- pgvector 0.8.7 installed
- `ragdb` created
- Vector extension enabled
- Git repository initialized

**Current milestone**

Phase 2 — Vector Database

**Next task**

PostgreSQL schema created with pgvector 768-dimensional embeddings.