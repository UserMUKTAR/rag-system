# Local PDF RAG System

A backend-focused Retrieval-Augmented Generation (RAG) system built with
Java and Spring Boot.

The system allows users to upload PDF documents, extract and chunk their
content, generate embeddings locally, store those embeddings in PostgreSQL
with pgvector, retrieve relevant content using semantic similarity, and
eventually generate grounded answers using a local Large Language Model.

## Project Goal

Build a complete RAG pipeline without relying on paid APIs.

## Architecture

```text
                ┌─────────────────┐
                │    PDF Upload   │
                └────────┬────────┘
                         │
                         ▼
                ┌─────────────────┐
                │    PDFBox       │
                │  Text Extraction│
                └────────┬────────┘
                         │
                         ▼
                ┌─────────────────┐
                │   Text Chunker  │
                └────────┬────────┘
                         │
                         ▼
                ┌─────────────────┐
                │ Ollama Embedding│
                │ nomic-embed-text│
                └────────┬────────┘
                         │
                         ▼
              ┌──────────────────────┐
              │ PostgreSQL + pgvector│
              └──────────┬───────────┘
                         │
                  Semantic Search
                         │
                         ▼
              ┌──────────────────────┐
              │ Relevant PDF Chunks  │
              └──────────┬───────────┘
                         │
                         ▼
              ┌──────────────────────┐
              │     Local LLM        │
              └──────────┬───────────┘
                         │
                         ▼
                    Final Answer