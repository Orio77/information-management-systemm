package com.orio77.information_management_systemm.processing.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.ai.chat.model.Generation;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orio77.information_management_systemm.core.util.AIUtil;
import com.orio77.information_management_systemm.processing.Explanation;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ExplanationFactoryService {

    private final String STEP_BY_STEP_REASONING_KEY = "step_by_step_reasoning";
    private final String FINAL_EXPLANATION_KEY = "final_explanation";

    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<Explanation> createExplanationsFromGenerations(List<Generation> generations, Long ideaId) {
        List<Explanation> explanations = new ArrayList<>();

        // Extract all JSON strings from the generations
        List<String> jsonStrings = AIUtil.extractJSONFromResponse(generations);

        for (String jsonString : jsonStrings) {
            try {
                JsonNode jsonNode = objectMapper.readTree(jsonString);

                // Extract the fields from each tuple
                String context;
                if (jsonNode.has(STEP_BY_STEP_REASONING_KEY)) {
                    log.info("Found step_by_step_reasoning in JSON.");
                    JsonNode contextNode = jsonNode.get(STEP_BY_STEP_REASONING_KEY);
                    // If it's an array or object, serialize it to JSON string
                    if (contextNode.isArray() || contextNode.isObject()) {
                        context = objectMapper.writeValueAsString(contextNode);
                    } else {
                        context = contextNode.asText();
                    }
                    log.debug("Extracted context: {}", context.isEmpty() ? "(none)" : context);
                } else {
                    log.error("{} key not found in JSON: {}", STEP_BY_STEP_REASONING_KEY, jsonString);
                    context = "";
                }
                String content;
                if (jsonNode.has(FINAL_EXPLANATION_KEY)) {
                    log.info("Found final_explanation in JSON.");
                    JsonNode contentNode = jsonNode.get(FINAL_EXPLANATION_KEY);
                    // If it's an array or object, serialize it to JSON string
                    if (contentNode.isArray() || contentNode.isObject()) {
                        content = objectMapper.writeValueAsString(contentNode);
                    } else {
                        content = contentNode.asText();
                    }
                    log.debug("Extracted content: {}", content.isEmpty() ? "(none)" : content);
                } else {
                    log.error("{} key not found in JSON: {}", FINAL_EXPLANATION_KEY, jsonString);
                    content = "";
                }

                Explanation explanation = new Explanation();
                explanation.setContext(context);
                explanation.setContent(content);
                explanation.setIdeaId(ideaId);
                explanations.add(explanation);

                log.debug("Created explanation: \n{} \n\nwith context: \n{}", content.isEmpty() ? "(none)" : content,
                        context.isEmpty() ? "(none)" : context);
            } catch (Exception e) {
                log.error("Failed to parse JSON: {}", jsonString, e);
            }
        }

        return explanations;
    }
}
