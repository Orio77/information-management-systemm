package com.orio77.information_management_systemm.processing.impl;

import java.util.List;

import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

import com.orio77.information_management_systemm.core.util.AIUtil;
import com.orio77.information_management_systemm.processing.InformationProcessingService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class InformationProcessingServiceImpl implements InformationProcessingService {

    private final ChatModel chatModel;

    private final static String PROCESS_INFO_SYSTEM_MESSAGE = """
                    You are a Logic Mapper. Your task is to reconstruct the logical bridge between a raw text and a distilled concept.
            """;

    private final static String PROCESS_INFO_USER_MESSAGE = """
                        Instructions: I have extracted a core concept from the text below. I need you to explain the logic the author used to arrive at this conclusion.
            Let's think step by step:
            1. First, identify where this concept appears or is alluded to in the text.
            2. Look for the "premises" — the sentences immediately preceding or following the concept that act as setup or justification.
            3. Look for "connectors" — words like "because," "therefore," "due to," or examples that illustrate the concept.
            4. Finally, write a paragraph starting with "The author argues this because..."
            Inputs: <concept> #### 3. Price’s Law: The Non-Linearity of Success
            In many systems, the square root of the total number of people in a domain does 50%% of the work. If you have 10 employees, 3 do half the work. If you have 10,000, 100 do half the work. This is a "Winner-Take-All" distribution.
            *   **The Lesson:** Success is non-linear. Once you start winning, the "Matthew Principle" takes over: "To those who have everything, more will be given." Small advantages at the beginning of a cycle (like a slightly better posture or a small win) compound into massive differences over time. Conversely, failures also compound. </concept>
            Output: Please provide your Step-by-Step reasoning first, followed by the Final Explanation. Use JSON format with 'step_by_step_reasoning' and 'final_explanation' nodes.
            Concept: %s
            Original_Text: %s
                        """;

    @Override
    public List<List<Generation>> processInformation(List<String> information, String sourceContent) {

        return information.stream().peek(info -> log.debug("Processing the following piece: {}", info))
                .map(info -> buildExtractionPrompt(info, sourceContent)).map(chatModel::call)
                .peek(response -> AIUtil.logResponse(response, log)).map(ChatResponse::getResults).toList();
    }

    private Prompt buildExtractionPrompt(String idea, String text) {
        // Construct the user message by inserting the text
        String userMessage = String.format(PROCESS_INFO_USER_MESSAGE, idea, text);

        // Return the constructed prompt
        return Prompt.builder().messages(List.of(
                SystemMessage.builder().text(PROCESS_INFO_SYSTEM_MESSAGE).build(),
                UserMessage.builder().text(userMessage).build())).build();
    }

    @Override
    public List<Generation> processInformation(String information, String sourceContent) {
        log.debug("Processing single piece of information: {}",
                information.substring(0, Math.min(information.length(), 100)));

        // Build the extraction prompt
        Prompt extractionPrompt = buildExtractionPrompt(information, sourceContent);

        // Call the chat model with the prompt
        ChatResponse response = chatModel.call(extractionPrompt);

        // Log the response for debugging
        AIUtil.logResponse(response, log);

        // Return the generations from the response
        return response.getResults();
    }

}
