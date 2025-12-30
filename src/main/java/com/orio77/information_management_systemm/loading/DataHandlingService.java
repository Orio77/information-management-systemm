package com.orio77.information_management_systemm.loading;

import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;

public interface DataHandlingService {
    
    public List<PDDocument> loadData();

    public PDDocument loadFile();
}
