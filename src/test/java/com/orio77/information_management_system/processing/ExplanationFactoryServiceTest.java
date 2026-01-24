package com.orio77.information_management_system.processing;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.Generation;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;

import com.orio77.information_management_system.processing.impl.ExplanationFactoryService;

public class ExplanationFactoryServiceTest {

    private ExplanationFactoryService explanationFactoryService = new ExplanationFactoryService();
    private Generation generation;
    private int EXPECTED_EXPLANATION_COUNT = 1;

    @BeforeEach
    public void setUp() throws IOException {
        // Read Json string from file
        ClassPathResource resource = new ClassPathResource("data/processed_info.json");
        String jsonString = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);

        // Wrap in markdown code blocks to simulate AI response format
        String wrappedJson = "```json\n" + jsonString + "\n```";

        // Create a generation
        AssistantMessage message = AssistantMessage.builder().content(wrappedJson).build();
        generation = new Generation(message);
    }

    @Test
    public void testExplanationCreation() {
        // This is a placeholder for actual test methods that would utilize the setup
        List<Explanation> explanations = explanationFactoryService
                .createExplanationsFromGenerations(List.of(generation), 1L);

        assertEquals(EXPECTED_EXPLANATION_COUNT, explanations.size());
    }
}
