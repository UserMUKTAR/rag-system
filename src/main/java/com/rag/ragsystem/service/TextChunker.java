package com.rag.ragsystem.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class TextChunker {

    private static final int CHUNK_SIZE = 500;
    private static final int CHUNK_OVERLAP = 50;

    public List<String> chunkText(String text) {

        String[] words = text.trim().split("\\s+");

        List<String> chunks = new ArrayList<>();

        int start = 0;

        while (start < words.length) {

            int end = Math.min(start + CHUNK_SIZE, words.length);

            String chunk = String.join(
                    " ",
                    Arrays.copyOfRange(words, start, end)
            );

            chunks.add(chunk);

            if (end == words.length) {
                break;
            }

            start = end - CHUNK_OVERLAP;
        }

        return chunks;
    }
}