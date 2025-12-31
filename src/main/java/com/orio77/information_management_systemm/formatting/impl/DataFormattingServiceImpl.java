package com.orio77.information_management_systemm.formatting.impl;

import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.stereotype.Service;

import com.orio77.information_management_systemm.formatting.DataFormattingService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DataFormattingServiceImpl implements DataFormattingService {
    
    @Override
    public PDDocument formatData(PDDocument data) {
        log.info("Formatting data: {}", data.getDocumentId());
        return data;
    }

    @Override
    public List<PDDocument> formatData(List<PDDocument> data) {
        log.info("Formatting list of data documents, count: {}", data.size());
        return data;
    }
}
