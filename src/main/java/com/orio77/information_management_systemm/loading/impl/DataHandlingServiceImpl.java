package com.orio77.information_management_systemm.loading.impl;

import org.springframework.stereotype.Service;

import com.orio77.information_management_systemm.loading.DataHandlingService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DataHandlingServiceImpl implements DataHandlingService {

    @Override
    public String loadData(String source) {
        // Implementation for loading data from the specified source
        log.info("Loading data from source: {}", source);
        return "Data loaded from " + source;
    }
    
}
