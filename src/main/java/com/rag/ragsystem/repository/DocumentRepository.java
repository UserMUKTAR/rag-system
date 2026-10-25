package com.rag.ragsystem.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rag.ragsystem.entity.Document;

public interface DocumentRepository extends JpaRepository<Document, Long> {

	Optional<Document> findBySha256Hash(String sha256Hash);

	boolean existsBySha256Hash(String sha256Hash);
}
