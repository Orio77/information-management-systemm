package com.orio77.information_management_systemm.extraction;

import java.util.List;

import org.springframework.ai.chat.model.Generation;

public interface InformationExtractionService {

    public List<List<Generation>> extractInformation(List<String> data);

    public List<Generation> extractInformation(String data);

}
