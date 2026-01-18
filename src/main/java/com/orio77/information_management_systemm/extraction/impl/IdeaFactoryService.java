package com.orio77.information_management_systemm.extraction.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.ai.chat.model.Generation;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orio77.information_management_systemm.core.util.AIUtil;
import com.orio77.information_management_systemm.extraction.Idea;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class IdeaFactoryService {

    private final String CONCEPT_KEY = "concept";
    private final String DEFINITION_KEY = "definition";
    private final String ACTIONABLE_TAKEAWAY_KEY = "actionable_takeaway";

    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<Idea> createIdeasFromGenerations(List<Generation> generations, Long sourceId) {
        List<Idea> ideas = new ArrayList<>();

        // Extract all JSON strings from the generations
        List<String> jsonStrings = AIUtil.extractJSONFromResponse(generations);
        log.debug("Extracted {} JSON strings from generations", jsonStrings.size());

        for (String jsonString : jsonStrings) {
            try {
                JsonNode jsonNode = objectMapper.readTree(jsonString);
                log.debug("Parsing JSON node: {}", jsonNode.toPrettyString());

                // Check if this is the wrapper object with "concepts" array
                if (jsonNode.has("concepts") && jsonNode.get("concepts").isArray()) {
                    log.debug("Found 'concepts' array with {} elements", jsonNode.get("concepts").size());
                    JsonNode conceptsArray = jsonNode.get("concepts");

                    for (JsonNode conceptNode : conceptsArray) {
                        Idea idea = createIdeaFromNode(conceptNode, sourceId);
                        ideas.add(idea);
                    }
                } else {
                    // Handle direct concept object (no wrapper)
                    log.debug("No 'concepts' wrapper found, treating as direct concept object");
                    Idea idea = createIdeaFromNode(jsonNode, sourceId);
                    ideas.add(idea);
                }
            } catch (Exception e) {
                log.error("Failed to parse JSON: {}", jsonString, e);
            }
        }

        log.info("Created {} ideas from generations", ideas.size());
        return ideas;
    }

    private Idea createIdeaFromNode(JsonNode conceptNode, Long sourceId) {
        // Extract the fields from the concept node
        String concept = conceptNode.has(CONCEPT_KEY) ? conceptNode.get(CONCEPT_KEY).asText() : "";
        String definition = conceptNode.has(DEFINITION_KEY) ? conceptNode.get(DEFINITION_KEY).asText() : "";
        String actionableTakeaway = conceptNode.has(ACTIONABLE_TAKEAWAY_KEY)
                ? conceptNode.get(ACTIONABLE_TAKEAWAY_KEY).asText()
                : "";

        log.debug("Extracted concept: '{}', definition length: {}, takeaway length: {}",
                concept, definition.length(), actionableTakeaway.length());

        // Combine them into a formatted content string
        String content = formatIdeaContent(concept, definition, actionableTakeaway);

        Idea idea = new Idea();
        idea.setContent(content);
        idea.setSourceId(sourceId);

        log.info("Created idea for concept: {}", concept);
        return idea;
    }

    private String formatIdeaContent(String concept, String definition, String actionableTakeaway) {
        StringBuilder sb = new StringBuilder();
        sb.append("Concept: ").append(concept).append("\n\n");
        sb.append("Definition: ").append(definition).append("\n\n");
        sb.append("Actionable Takeaway: ").append(actionableTakeaway);
        return sb.toString();
    }
}
