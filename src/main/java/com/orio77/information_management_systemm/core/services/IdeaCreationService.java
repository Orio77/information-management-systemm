package com.orio77.information_management_systemm.core.services;

import java.util.List;

import org.springframework.ai.chat.model.Generation;

import com.orio77.information_management_systemm.extraction.Idea;
import com.orio77.information_management_systemm.loading.FileData;
import com.orio77.information_management_systemm.ordering.IdeaPrerequisite;
import com.orio77.information_management_systemm.processing.Explanation;

public interface IdeaCreationService {

    default void createIdeas(Long sourceId) {
        FileData data = loadFileDataById(sourceId);
        List<Generation> extractedInformation = extractInformation(data.getContent());
        String formattedData = data.getContent();

        extractedInformation.stream()
                .map(generation -> {
                    List<Idea> ideas = createIdeasFromGenerations(generation, (long) data.hashCode());
                    saveIdeas(ideas);
                    List<IdeaPrerequisite> ideaPrerequisites = orderIdeas(ideas);
                    saveIdeaPrerequisites(ideaPrerequisites);
                    return ideas;
                }).flatMap(List::stream)
                .forEach(idea -> {
                    List<Generation> explanationGenerations = processInformation(idea.getContent(), formattedData);
                    List<Explanation> explanations = createExplanationsFromGenerations(explanationGenerations,
                            idea.getId());
                    saveExplanations(explanations);
                });
    }

    FileData loadFileDataById(Long id);

    List<Generation> extractInformation(String data);

    List<Idea> createIdeasFromGenerations(Generation generation, Long sourceId);

    void saveIdeas(List<Idea> ideas);

    List<Generation> processInformation(String content, String data);

    List<Explanation> createExplanationsFromGenerations(List<Generation> generations, Long ideaId);

    void saveExplanations(List<Explanation> explanations);

    List<IdeaPrerequisite> orderIdeas(List<Idea> ideas);

    void saveIdeaPrerequisites(List<IdeaPrerequisite> ideaPrerequisites);

}