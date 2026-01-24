package com.orio77.information_management_system.core.services.impl;

import java.util.List;

import org.springframework.ai.chat.model.Generation;
import org.springframework.stereotype.Service;

import com.orio77.information_management_system.core.services.IdeaCreationService;
import com.orio77.information_management_system.extraction.Idea;
import com.orio77.information_management_system.extraction.IdeaRepository;
import com.orio77.information_management_system.extraction.InformationExtractionService;
import com.orio77.information_management_system.extraction.impl.IdeaFactoryService;
import com.orio77.information_management_system.loading.FileData;
import com.orio77.information_management_system.loading.FileDataRepository;
import com.orio77.information_management_system.ordering.IdeaPrerequisite;
import com.orio77.information_management_system.ordering.IdeaPrerequisiteRepository;
import com.orio77.information_management_system.ordering.InformationOrderingService;
import com.orio77.information_management_system.processing.Explanation;
import com.orio77.information_management_system.processing.ExplanationRepository;
import com.orio77.information_management_system.processing.InformationProcessingService;
import com.orio77.information_management_system.processing.impl.ExplanationFactoryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class IdeaCreationServiceImpl implements IdeaCreationService {

    private final FileDataRepository fileDataRepo;

    private final InformationExtractionService informationExtractionService;

    private final IdeaFactoryService ideaFactoryService;

    private final IdeaRepository ideaRepo;

    private final InformationProcessingService informationProcessingService;

    private final ExplanationFactoryService explanationFactoryService;

    private final ExplanationRepository explanationRepo;

    private final InformationOrderingService informationOrderingService;

    private final IdeaPrerequisiteRepository ideaPrerequisiteRepo;

    @Override
    public FileData loadFileDataById(Long id) {
        return fileDataRepo.getReferenceById(id);
    }

    @Override
    public List<Generation> extractInformation(String data) {
        return informationExtractionService.extractInformation(data);
    }

    @Override
    public List<Idea> createIdeasFromGenerations(Generation generation, Long sourceId) {
        return ideaFactoryService.createIdeasFromGenerations(List.of(generation), sourceId);
    }

    @Override
    public void saveIdeas(List<Idea> ideas) {
        ideaRepo.saveAll(ideas);
    }

    @Override
    public List<Generation> processInformation(String content, String data) {
        return informationProcessingService.processInformation(content, data);
    }

    @Override
    public List<Explanation> createExplanationsFromGenerations(List<Generation> generation, Long ideaId) {
        return explanationFactoryService.createExplanationsFromGenerations(generation, ideaId);
    }

    @Override
    public void saveExplanations(List<Explanation> explanations) {
        explanationRepo.saveAll(explanations);
    }

    @Override
    public List<IdeaPrerequisite> orderIdeas(List<Idea> ideas) {
        return informationOrderingService.orderInformation(ideas);
    }

    @Override
    public void saveIdeaPrerequisites(List<IdeaPrerequisite> ideaPrerequisites) {
        ideaPrerequisiteRepo.saveAll(ideaPrerequisites);
    }
}
