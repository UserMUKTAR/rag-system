package com.rag.ragsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rag.ragsystem.entity.DocumentChunk;

public interface DocumentChunkRepository extends JpaRepository<DocumentChunk, Long> {

}