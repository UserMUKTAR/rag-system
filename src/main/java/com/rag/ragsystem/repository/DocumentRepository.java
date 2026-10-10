package com.rag.ragsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rag.ragsystem.entity.Document;

public interface DocumentRepository extends JpaRepository<Document, Long> {

}
