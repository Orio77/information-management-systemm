package com.orio77.information_management_system.extraction;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
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

import com.orio77.information_management_system.extraction.impl.InformationExtractionServiceImpl;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@DisplayName("Information Extraction Service Tests")
class InformationExtractionServiceTest {

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

    private InformationExtractionService extractionService;

    @BeforeEach
    void setUp() {
        extractionService = new InformationExtractionServiceImpl(chatModel);

        // Configure mocks to prevent NPE in AIUtil.logResponse (lenient because not all
        // tests use all mocks)
        lenient().when(generation1.getOutput()).thenReturn(assistantMessage1);
        lenient().when(generation2.getOutput()).thenReturn(assistantMessage2);
        lenient().when(assistantMessage1.getText()).thenReturn("Mock response 1");
        lenient().when(assistantMessage2.getText()).thenReturn("Mock response 2");
    }

    @Test
    @DisplayName("Should extract information from single document")
    void shouldExtractInformationFromSingleDocument() {
        // Given
        String inputText = "Sample text about multitasking and cognitive load.";
        List<String> data = List.of(inputText);

        when(chatResponse1.getResults()).thenReturn(List.of(generation1));
        when(chatModel.call(any(Prompt.class))).thenReturn(chatResponse1);

        // When
        List<List<Generation>> results = extractionService.extractInformation(data);

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
    @DisplayName("Should extract information from multiple documents")
    void shouldExtractInformationFromMultipleDocuments() {
        // Given
        List<String> data = List.of(
                "First document about focus and attention.",
                "Second document about productivity and time management.");

        when(chatResponse1.getResults()).thenReturn(List.of(generation1));
        when(chatResponse2.getResults()).thenReturn(List.of(generation2));
        when(chatModel.call(any(Prompt.class)))
                .thenReturn(chatResponse1)
                .thenReturn(chatResponse2);

        // When
        List<List<Generation>> results = extractionService.extractInformation(data);

        // Then
        assertThat(results).hasSize(2);
        assertThat(results.get(0)).containsExactly(generation1);
        assertThat(results.get(1)).containsExactly(generation2);

        verify(chatModel, times(2)).call(any(Prompt.class));
    }

    @Test
    @DisplayName("Should return empty list when no data provided")
    void shouldReturnEmptyListWhenNoDataProvided() {
        // Given
        List<String> emptyData = List.of();

        // When
        List<List<Generation>> results = extractionService.extractInformation(emptyData);

        // Then
        assertThat(results).isEmpty();
        verify(chatModel, times(0)).call(any(Prompt.class));
    }

    @Test
    @DisplayName("Should build correct prompt with system and user messages")
    void shouldBuildCorrectPromptWithSystemAndUserMessages() {
        // Given
        String inputText = "Test text for prompt building.";
        List<String> data = List.of(inputText);

        when(chatResponse1.getResults()).thenReturn(List.of(generation1));
        when(chatModel.call(promptCaptor.capture())).thenReturn(chatResponse1);

        // When
        extractionService.extractInformation(data);

        // Then
        Prompt capturedPrompt = promptCaptor.getValue();
        assertThat(capturedPrompt.getInstructions()).hasSize(2);
        assertThat(capturedPrompt.getInstructions().get(0).getText())
                .contains("expert data archivist");
        assertThat(capturedPrompt.getInstructions().get(1).getText())
                .contains(inputText)
                .contains("Extract core ideas");
    }

    @Test
    @DisplayName("Should handle multiple generations per document")
    void shouldHandleMultipleGenerationsPerDocument() {
        // Given
        String inputText = "Complex document requiring multiple extractions.";
        List<String> data = List.of(inputText);

        List<Generation> multipleGenerations = List.of(generation1, generation2);
        when(chatResponse1.getResults()).thenReturn(multipleGenerations);
        when(chatModel.call(any(Prompt.class))).thenReturn(chatResponse1);

        // When
        List<List<Generation>> results = extractionService.extractInformation(data);

        // Then
        assertThat(results)
                .hasSize(1)
                .first()
                .asInstanceOf(InstanceOfAssertFactories.list(Generation.class))
                .hasSize(2)
                .containsExactly(generation1, generation2);
    }

    @Test
    @DisplayName("Should process documents in order")
    void shouldProcessDocumentsInOrder() {
        // Given
        List<String> data = List.of("Doc1", "Doc2", "Doc3");

        when(chatResponse1.getResults()).thenReturn(List.of(generation1));
        when(chatResponse2.getResults()).thenReturn(List.of(generation2));
        when(chatModel.call(any(Prompt.class)))
                .thenReturn(chatResponse1)
                .thenReturn(chatResponse2)
                .thenReturn(chatResponse1);

        // When
        List<List<Generation>> results = extractionService.extractInformation(data);

        // Then
        assertThat(results).hasSize(3);
        verify(chatModel, times(3)).call(any(Prompt.class));
    }

    @Nested
    @SpringBootTest
    @ActiveProfiles("test")
    @DisplayName("Integration Tests")
    class IntegrationTests {

        @Autowired
        private InformationExtractionService extractionService;

        private List<String> testData;
        private boolean sampleDataLoaded = false;

        @BeforeEach
        void setUp() {
            try {
                testData = List.of(new String(getClass()
                        .getResourceAsStream("/data/test-file.txt").readAllBytes()));
                sampleDataLoaded = true;
            } catch (Exception e) {
                sampleDataLoaded = false;
            }
        }

        @Test
        @DisplayName("Should extract information from sample data file")
        void shouldExtractInformationFromSampleDataFile() {
            assumeTrue(sampleDataLoaded, "Sample data file not available - skipping test");

            // When
            List<List<Generation>> information = extractionService.extractInformation(testData);

            // Then
            assertThat(information)
                    .isNotEmpty()
                    .hasSize(testData.size());

            // Verify actual results are returned
            assertThat(information.get(0))
                    .isNotEmpty()
                    .allMatch(generation -> generation.getOutput() != null);
        }
    }
}
