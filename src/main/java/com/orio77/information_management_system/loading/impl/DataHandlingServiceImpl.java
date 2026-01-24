package com.orio77.information_management_system.loading.impl;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.orio77.information_management_system.loading.DataHandlingService;
import com.orio77.information_management_system.loading.FileData;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DataHandlingServiceImpl implements DataHandlingService {

    private final static String filePath = "src/main/resources/data/12-Rules-for-Life-74-88.pdf";

    private final static String dataPath = "src/main/resources/data/";

    @Override
    public FileData loadFile() {
        log.info("Loading data from file: {}", filePath);

        try {
            File file = new File(filePath);
            // Load PDF document
            PDDocument doc = Loader.loadPDF(file);

            // Log information about the loaded PDF
            int numberOfPages = doc.getNumberOfPages();
            log.info("PDF loaded successfully with {} pages.", numberOfPages);
            log.info("PDF Title: {}", doc.getDocumentInformation().getTitle());
            log.info("PDF Author: {}", doc.getDocumentInformation().getAuthor());

            // Return the loaded document
            PDFTextStripper stripper = new PDFTextStripper();

            String content = stripper.getText(doc);
            String title = (doc.getDocumentInformation().getTitle() == null
                    || doc.getDocumentInformation().getTitle().strip().isBlank()) ? file.getName()
                            : doc.getDocumentInformation()
                                    .getTitle();

            return new FileData(title, content, numberOfPages);

        } catch (IOException e) {
            // Log error if loading fails
            log.error("Error loading PDF file: {}", e.getMessage());
            // Rethrow as a runtime exception
            throw new RuntimeException(e);
        }
    }

    @Override
    public FileData loadFile(String filePath) {
        log.info("Loading data from file: {}", filePath);

        try {
            File file = new File(filePath);
            // Load PDF document
            PDDocument doc = Loader.loadPDF(file);

            // Log information about the loaded PDF
            int numberOfPages = doc.getNumberOfPages();
            log.info("PDF loaded successfully with {} pages.", numberOfPages);
            log.info("PDF Title: {}", doc.getDocumentInformation().getTitle());
            log.info("PDF Author: {}", doc.getDocumentInformation().getAuthor());

            // Return the loaded document
            PDFTextStripper stripper = new PDFTextStripper();

            String content = stripper.getText(doc);
            String title = (doc.getDocumentInformation().getTitle() == null
                    || doc.getDocumentInformation().getTitle().strip().isBlank()) ? file.getName()
                            : doc.getDocumentInformation()
                                    .getTitle();

            return new FileData(title, content, numberOfPages);

        } catch (IOException e) {
            // Log error if loading fails
            log.error("Error loading PDF file: {}", e.getMessage());
            // Rethrow as a runtime exception
            throw new RuntimeException(e);
        }
    }

    @Override
    public FileData loadFile(MultipartFile file) {
        log.info("Loading data from file: {}", file.getOriginalFilename());

        try {
            // Load PDF document
            PDDocument doc = Loader.loadPDF(file.getBytes());

            // Log information about the loaded PDF
            int numberOfPages = doc.getNumberOfPages();
            log.info("PDF loaded successfully with {} pages.", numberOfPages);
            log.info("PDF Title: {}", doc.getDocumentInformation().getTitle());
            log.info("PDF Author: {}", doc.getDocumentInformation().getAuthor());

            // Return the loaded document
            PDFTextStripper stripper = new PDFTextStripper();

            String content = stripper.getText(doc);
            String title = (doc.getDocumentInformation().getTitle() == null
                    || doc.getDocumentInformation().getTitle().strip().isBlank()) ? file.getOriginalFilename()
                            : doc.getDocumentInformation()
                                    .getTitle();

            return new FileData(title, content, numberOfPages);

        } catch (IOException e) {
            // Log error if loading fails
            log.error("Error loading PDF file: {}", e.getMessage());
            // Rethrow as a runtime exception
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<FileData> loadData() {
        // Implementation for loading data from the specified source
        log.info("Loading data from source: {}", dataPath);

        // Create a File object for the data directory
        File dataDir = new File(dataPath);
        // List all PDF files in the directory
        File[] files = dataDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".pdf"));

        // Load each PDF file into a PDDocument and collect them into a list
        List<FileData> documents = java.util.Arrays.stream(files).map(File::getAbsolutePath).map(this::loadFile)
                .filter(doc -> doc != null).toList();

        log.info("Total PDFs loaded: {}", documents.size());

        // Return the list of loaded documents
        return documents;
    }
}
