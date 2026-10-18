package com.rag.ragsystem.service;

import java.io.IOException;
import java.io.InputStream;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PdfService {

    public String extractText(MultipartFile file) throws IOException {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException(
                    "The uploaded file must not be empty.");
        }

        try (InputStream inputStream = file.getInputStream();
             PDDocument document =
                     Loader.loadPDF(inputStream.readAllBytes())) {

            PDFTextStripper pdfTextStripper = new PDFTextStripper();

            return pdfTextStripper.getText(document);

        } catch (IOException ex) {
            throw new IllegalArgumentException(
                    "The uploaded file is not a valid or readable PDF.");
        }
    }
}