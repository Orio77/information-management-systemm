package com.orio77.information_management_system.formatting.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.orio77.information_management_system.formatting.DataFormattingService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DataFormattingServiceImpl implements DataFormattingService {

    @Override
    public String formatData(String data) {
        log.info("Formatting data: {}", data.substring(0, Math.min(30, data.length())) + "...");
        return data;
    }

    @Override
    public List<String> formatData(List<String> data) {
        log.info("Formatting list of data documents, count: {}", data.size());
        return data;
    }
}
