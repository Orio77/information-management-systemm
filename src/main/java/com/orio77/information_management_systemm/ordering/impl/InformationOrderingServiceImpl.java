package com.orio77.information_management_systemm.ordering.impl;

import org.springframework.stereotype.Service;

import com.orio77.information_management_systemm.ordering.InformationOrderingService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class InformationOrderingServiceImpl implements InformationOrderingService {

    @Override
    public String orderInformation() {
        // Implementation for ordering the provided information
        log.info("Ordering information");
        return "Ordered information";
    }
}
