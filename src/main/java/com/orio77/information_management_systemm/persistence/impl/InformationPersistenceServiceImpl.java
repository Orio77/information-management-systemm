package com.orio77.information_management_systemm.persistence.impl;

import org.springframework.stereotype.Service;

import com.orio77.information_management_systemm.persistence.InformationPersistenceService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class InformationPersistenceServiceImpl implements InformationPersistenceService {
    
    @Override
    public String persistInformation(String information) {
        // Implementation for persisting the provided information
        log.info("Persisting information: {}", information);
        return "Persisted information: " + information;
    }
}
