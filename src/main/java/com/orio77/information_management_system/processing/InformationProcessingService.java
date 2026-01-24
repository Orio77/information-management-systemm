package com.orio77.information_management_system.processing;

import java.util.List;

import org.springframework.ai.chat.model.Generation;

public interface InformationProcessingService {

    public List<List<Generation>> processInformation(List<String> information, String sourceContent);

    public List<Generation> processInformation(String information, String sourceContent);
}
