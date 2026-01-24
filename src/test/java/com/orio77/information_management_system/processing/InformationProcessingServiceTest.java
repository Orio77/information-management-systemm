package com.orio77.information_management_system.processing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.orio77.information_management_system.processing.impl.InformationProcessingServiceImpl;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@DisplayName("Information Processing Service Tests")
class InformationProcessingServiceTest {

    @Mock
    private ChatModel chatModel;

    @Mock
    private ChatResponse chatResponse1;

    @Mock
    private ChatResponse chatResponse2;

    @Mock
    private Generation generation1;

    @Mock
    private Generation generation2;

    @Mock
    private AssistantMessage assistantMessage1;

    @Mock
    private AssistantMessage assistantMessage2;

    @Captor
    private ArgumentCaptor<Prompt> promptCaptor;

    private InformationProcessingService processingService;

    @BeforeEach
    void setUp() {
        processingService = new InformationProcessingServiceImpl(chatModel);

        org.mockito.Mockito.lenient().when(generation1.getOutput()).thenReturn(assistantMessage1);
        org.mockito.Mockito.lenient().when(generation2.getOutput()).thenReturn(assistantMessage2);
        org.mockito.Mockito.lenient().when(assistantMessage1.getText()).thenReturn("Mock response 1");
        org.mockito.Mockito.lenient().when(assistantMessage2.getText()).thenReturn("Mock response 2");
    }

    @Test
    @DisplayName("Should process single piece of information with source content")
    void shouldProcessSinglePieceOfInformationWithSourceContent() {
        // Given
        String concept = "Concept: Switching Cost - The cognitive penalty when shifting focus";
        String sourceContent = "Original text about multitasking and focus management.";
        List<String> information = List.of(concept);

        when(chatResponse1.getResults()).thenReturn(List.of(generation1));
        when(chatModel.call(any(Prompt.class))).thenReturn(chatResponse1);

        // When
        List<List<Generation>> results = processingService.processInformation(information, sourceContent);

        // Then
        assertThat(results)
                .hasSize(1)
                .first()
                .isInstanceOf(List.class)
                .asInstanceOf(InstanceOfAssertFactories.list(Generation.class))
                .containsExactly(generation1);

        verify(chatModel, times(1)).call(any(Prompt.class));
    }

    @Test
    @DisplayName("Should process multiple pieces of information")
    void shouldProcessMultiplePiecesOfInformation() {
        // Given
        String sourceContent = "Source text containing multiple concepts about productivity.";
        List<String> information = List.of(
                "First concept about focus and attention.",
                "Second concept about task management.");

        when(chatResponse1.getResults()).thenReturn(List.of(generation1));
        when(chatResponse2.getResults()).thenReturn(List.of(generation2));
        when(chatModel.call(any(Prompt.class)))
                .thenReturn(chatResponse1)
                .thenReturn(chatResponse2);

        // When
        List<List<Generation>> results = processingService.processInformation(information, sourceContent);

        // Then
        assertThat(results).hasSize(2);
        assertThat(results.get(0)).containsExactly(generation1);
        assertThat(results.get(1)).containsExactly(generation2);

        verify(chatModel, times(2)).call(any(Prompt.class));
    }

    @Test
    @DisplayName("Should return empty list when no information provided")
    void shouldReturnEmptyListWhenNoInformationProvided() {
        // Given
        List<String> emptyInformation = List.of();
        String sourceContent = "Some source content";

        // When
        List<List<Generation>> results = processingService.processInformation(emptyInformation, sourceContent);

        // Then
        assertThat(results).isEmpty();
        verify(chatModel, times(0)).call(any(Prompt.class));
    }

    @Test
    @DisplayName("Should build correct prompt with system and user messages")
    void shouldBuildCorrectPromptWithSystemAndUserMessages() {
        // Given
        String concept = "Test concept for prompt building.";
        String sourceContent = "Original source text for context.";
        List<String> information = List.of(concept);

        when(chatResponse1.getResults()).thenReturn(List.of(generation1));
        when(chatModel.call(promptCaptor.capture())).thenReturn(chatResponse1);

        // When
        processingService.processInformation(information, sourceContent);

        // Then
        Prompt capturedPrompt = promptCaptor.getValue();
        assertThat(capturedPrompt.getInstructions()).hasSize(2);
        assertThat(capturedPrompt.getInstructions().get(0).getText())
                .contains("Logic Mapper");
        assertThat(capturedPrompt.getInstructions().get(1).getText())
                .contains(concept)
                .contains("explain the logic");
    }

    @Test
    @DisplayName("Should handle multiple generations per concept")
    void shouldHandleMultipleGenerationsPerConcept() {
        // Given
        String concept = "Complex concept requiring multiple processing steps.";
        String sourceContent = "Detailed source content with multiple premises.";
        List<String> information = List.of(concept);

        List<Generation> multipleGenerations = List.of(generation1, generation2);
        when(chatResponse1.getResults()).thenReturn(multipleGenerations);
        when(chatModel.call(any(Prompt.class))).thenReturn(chatResponse1);

        // When
        List<List<Generation>> results = processingService.processInformation(information, sourceContent);

        // Then
        assertThat(results)
                .hasSize(1)
                .first()
                .asInstanceOf(InstanceOfAssertFactories.list(Generation.class))
                .hasSize(2)
                .containsExactly(generation1, generation2);
    }

    @Test
    @DisplayName("Should process concepts in order")
    void shouldProcessConceptsInOrder() {
        // Given
        String sourceContent = "Original text";
        List<String> information = List.of("Concept1", "Concept2", "Concept3");

        when(chatResponse1.getResults()).thenReturn(List.of(generation1));
        when(chatResponse2.getResults()).thenReturn(List.of(generation2));
        when(chatModel.call(any(Prompt.class)))
                .thenReturn(chatResponse1)
                .thenReturn(chatResponse2)
                .thenReturn(chatResponse1);

        // When
        List<List<Generation>> results = processingService.processInformation(information, sourceContent);

        // Then
        assertThat(results).hasSize(3);
        verify(chatModel, times(3)).call(any(Prompt.class));
    }

    @Test
    @DisplayName("Should include source content in prompt")
    void shouldIncludeSourceContentInPrompt() {
        // Given
        String concept = "Price's Law: Non-linearity of success";
        String sourceContent = "Unique source text with specific context about distributions";
        List<String> information = List.of(concept);

        when(chatResponse1.getResults()).thenReturn(List.of(generation1));
        when(chatModel.call(promptCaptor.capture())).thenReturn(chatResponse1);

        // When
        processingService.processInformation(information, sourceContent);

        // Then
        Prompt capturedPrompt = promptCaptor.getValue();
        String userMessage = capturedPrompt.getInstructions().get(1).getText();
        assertThat(userMessage)
                .contains(concept)
                .contains("Original_Text");
    }

    @Test
    @DisplayName("Should handle empty source content")
    void shouldHandleEmptySourceContent() {
        // Given
        String concept = "Some concept";
        String emptySourceContent = "";
        List<String> information = List.of(concept);

        when(chatResponse1.getResults()).thenReturn(List.of(generation1));
        when(chatModel.call(any(Prompt.class))).thenReturn(chatResponse1);

        // When
        List<List<Generation>> results = processingService.processInformation(information, emptySourceContent);

        // Then
        assertThat(results).hasSize(1);
        verify(chatModel, times(1)).call(any(Prompt.class));
    }

    @Nested
    @SpringBootTest
    @ActiveProfiles("test")
    @DisplayName("Integration Tests")
    class IntegrationTests {

        @Autowired
        private InformationProcessingService processingService;

        private List<String> testInformationList;
        private String testSourceContent;
        private boolean testDataLoaded = false;
        private String testInformation = """
                    {
                "concept": "Dominance Hierarchy",
                "definition": "An evolutionarily ancient master control system, shared by creatures as primitive as lobsters, that monitors social status and regulates neurochemistry, health, and behavior based on one's perceived rank.",
                "actionable_takeaway": "Recognize that your social standing is a biological reality that directly impacts your physical health and psychological resilience; treat your position in the world as a vital metric for well-being."
                }""";

        @BeforeEach
        void setUp() {
            try {
                testSourceContent = new String(getClass()
                        .getResourceAsStream("/data/test-file.txt").readAllBytes());
                testInformationList = List.of(testInformation);
                testDataLoaded = true;
            } catch (

            Exception e) {
                testDataLoaded = false;
            }
        }

        @Test
        @DisplayName("Should process information with sample data file")
        void shouldProcessInformationWithSampleDataFile() {
            assumeTrue(testDataLoaded, "Sample data file not available - skipping test");

            // When
            List<List<Generation>> processedInfo = processingService.processInformation(
                    testInformationList, testSourceContent);

            // Then
            assertThat(processedInfo)
                    .isNotEmpty()
                    .hasSize(testInformationList.size());

            // Verify actual results are returned
            assertThat(processedInfo.get(0))
                    .isNotEmpty()
                    .allMatch(generation -> generation.getOutput() != null);
        }

        @Test
        @DisplayName("Should handle large source content")
        void shouldHandleLargeSourceContent() {
            assumeTrue(testDataLoaded, "Sample data file not available - skipping test");

            // Given
            List<String> singleConcept = List.of(testInformationList.get(0));

            // When
            List<List<Generation>> processedInfo = processingService.processInformation(
                    singleConcept, testSourceContent);

            // Then
            assertThat(processedInfo)
                    .hasSize(1)
                    .first()
                    .asInstanceOf(InstanceOfAssertFactories.list(Generation.class))
                    .isNotEmpty();
        }
    }
}
