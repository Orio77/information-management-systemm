package com.orio77.information_management_systemm.loading.impl;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

import com.orio77.information_management_systemm.loading.DataHandlingService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DataHandlingServiceImpl implements DataHandlingService {

    private final static String filePath = "src/main/resources/data/12-Rules-for-Life-23-38.pdf";

    private final static String dataPath = "src/main/resources/data/";

    @Override
    public String loadFile() {
        log.info("Loading data from file: {}", filePath);

        try {
            // Load PDF document
            PDDocument doc = Loader.loadPDF(new File(filePath));

            // Log information about the loaded PDF
            int numberOfPages = doc.getNumberOfPages();
            log.info("PDF loaded successfully with {} pages.", numberOfPages);
            log.info("PDF Title: {}", doc.getDocumentInformation().getTitle());
            log.info("PDF Author: {}", doc.getDocumentInformation().getAuthor());

            // Return the loaded document
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(doc);
        } catch (IOException e) {
            // Log error if loading fails
            log.error("Error loading PDF file: {}", e.getMessage());
            // Rethrow as a runtime exception
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<String> loadData() {
        // Implementation for loading data from the specified source
        log.info("Loading data from source: {}", dataPath);

        // Create a File object for the data directory
        File dataDir = new File(dataPath);
        // List all PDF files in the directory
        File[] files = dataDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".pdf"));
        // Initialize PDFTextStripper for text extraction
        PDFTextStripper stripper = new PDFTextStripper();

        // Load each PDF file into a PDDocument and collect them into a list
        List<String> documents = files != null ? java.util.Arrays.stream(files).map(file -> {
            try {
                PDDocument doc = Loader.loadPDF(file);
                log.info("Loaded PDF: {} with {} pages.", file.getName(), doc.getNumberOfPages());
                return stripper.getText(doc);
            } catch (IOException e) {
                log.error("Error loading PDF file {}: {}", file.getName(), e.getMessage());
                return null;
            }
        }).filter(doc -> doc != null).toList()
                : List.of();

        log.info("Total PDFs loaded: {}", documents.size());

        // Return the list of loaded documents
        return documents;
    }
}
