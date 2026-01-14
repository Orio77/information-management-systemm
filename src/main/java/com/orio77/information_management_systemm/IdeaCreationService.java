package com.orio77.information_management_systemm;

import java.util.List;

import org.springframework.ai.chat.model.Generation;

import com.orio77.information_management_systemm.extraction.Idea;
import com.orio77.information_management_systemm.processing.Explanation;

public interface IdeaCreationService {

    default void createIdeas() {
        String data = loadData();
        String formattedData = formatData(data);
        List<Generation> extractedInformation = extractInformation(formattedData);

        extractedInformation.stream().limit(1)
                .map(generation -> {
                    List<Idea> ideas = createIdeasFromGenerations(generation,
                            Long.valueOf(String.valueOf(data.hashCode())));
                    saveIdeas(ideas);
                    return ideas;
                }).flatMap(List::stream).map(Idea::getContent)
                .map(ideaContent -> processInformation(ideaContent, formattedData)).map(explanationGeneration -> {
                    List<Explanation> explanations = createExplanationsFromGenerations(explanationGeneration, 1L);
                    saveExplanations(explanations);
                    return explanations;
                }).toList();
    }

    String loadData();

    String formatData(String data);

    List<Generation> extractInformation(String data);

    List<Idea> createIdeasFromGenerations(Generation generation, Long sourceId);

    void saveIdeas(List<Idea> ideas);

    List<Generation> processInformation(String content, String data);

    List<Explanation> createExplanationsFromGenerations(List<Generation> generation, Long ideaId);

    void saveExplanations(List<Explanation> explanations);

}
