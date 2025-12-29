package com.orio77.information_management_systemm.processing.impl;

import org.springframework.stereotype.Service;

import com.orio77.information_management_systemm.processing.InformationProcessingService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class InformationProcessingServiceImpl implements InformationProcessingService {
    
    @Override
    public String processInformation(String information) {
        // Implementation for processing the provided information
        log.info("Processing information: {}", information);
        return "Processed information: " + information;
    }
}
