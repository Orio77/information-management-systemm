package com.orio77.information_management_systemm.extraction;

import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;

public interface InformationExtractionService {
    
    public List<String> extractInformation(List<PDDocument> data);
}
