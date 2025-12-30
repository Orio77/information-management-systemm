package com.orio77.information_management_systemm.extraction.impl;

import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.stereotype.Service;

import com.orio77.information_management_systemm.extraction.InformationExtractionService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class InformationExtractionServiceImpl implements InformationExtractionService {
    
    @Override
    public String extractInformation(List<PDDocument> data) {
        // Implementation for extracting information from the provided data
        log.info("Extracting information from data: {}", data);
        return "Extracted information from data: " + data;
    }
}
