package com.orio77.information_management_systemm.formatting;

import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;

public interface DataFormattingService {
    
    public PDDocument formatData(PDDocument data);
    
    public List<PDDocument> formatData(List<PDDocument> data);
}
