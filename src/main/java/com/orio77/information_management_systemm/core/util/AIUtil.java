package com.orio77.information_management_systemm.core.util;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AIUtil {

        private static final Logger log = LoggerFactory.getLogger(AIUtil.class);

        public static void logResponse(ChatResponse response, org.slf4j.Logger log) {
                log.debug("Received response with {} results",
                                response.getResults().size());
                response.getResults().forEach(
                                gen -> log.debug("\n\nGeneration text: {}",
                                                gen.getOutput().getText()));
        }

        public static String extractFromResponse(
                        String response,
                        String startDelimiter,
                        String endDelimiter) {
                int startIndex = response.indexOf(startDelimiter);
                int endIndex = response.indexOf(
                                endDelimiter,
                                startIndex + startDelimiter.length());

                if (startIndex != -1 && endIndex != -1) {
                        return response
                                        .substring(startIndex + startDelimiter.length(), endIndex)
                                        .trim();
                } else {
                        return "";
                }
        }

        public static List<String> extractJSONFromResponse(List<Generation> response) {
                log.debug("Extracting JSON from response with {} generations", response != null ? response.size() : 0);

                if (response == null) {
                        log.error("Response is null, cannot extract JSON");
                        return new ArrayList<>();
                }

                List<String> jsonStrings = new ArrayList<>();
                String startDelimiter = "```json";
                String endDelimiter = "```";

                for (Generation gen : response) {
                        String genText = gen.getOutput().getText();
                        log.debug("Processing generation text of length: {}", genText.length());

                        // Ensure it's not the thinking block
                        if (!genText.contains(startDelimiter) || !genText.contains(endDelimiter)) {
                                log.debug("Generation does not contain JSON delimiters, skipping");
                                continue;
                        }

                        // Extract JSON blocks
                        String json = extractFromResponse(genText, startDelimiter, endDelimiter);
                        log.debug("Extracted JSON string of length: {}", json.length());

                        // If JSON was found, parse it
                        if (!json.isEmpty()) {
                                ObjectMapper mapper = new ObjectMapper();
                                try {
                                        JsonNode jsonNode = mapper.readTree(json);

                                        // Check if it's an array and iterate through each element
                                        if (jsonNode.isArray()) {
                                                log.debug("JSON is an array with {} elements", jsonNode.size());
                                                for (JsonNode element : jsonNode) {
                                                        jsonStrings.add(element.toString());
                                                }
                                        } else {
                                                // If it's a single object, add it directly
                                                log.debug("JSON is a single object");
                                                jsonStrings.add(jsonNode.toString());
                                        }
                                } catch (Exception e) {
                                        log.error("Failed to parse JSON: {}", json, e);
                                }
                        } else {
                                log.warn("No JSON content found between delimiters");
                        }
                }

                log.debug("Extracted {} JSON strings total", jsonStrings.size());
                return jsonStrings;
        }
}
