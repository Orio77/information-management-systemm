package com.orio77.information_management_systemm.extraction.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.orio77.information_management_systemm.extraction.InformationExtractionService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class InformationExtractionServiceImpl implements InformationExtractionService {

    @Autowired
    private ChatModel chatModel;

    private final static String EXTRACT_INFO_SYSTEM_MESSAGE = """
                    You are an expert data archivist. Your job is to convert unstructured text into a strict, structured database of mental models.
            """
                    
    ;

    private final static String EXTRACT_INFO_USER_MESSAGE = """
            Instructions: Extract core ideas from the text provided. Use the following examples to understand the required depth and formatting.
Examples: <example> Input: "Many people try to multitask, but the brain actually switches rapidly between tasks rather than doing them simultaneously. This switching cost lowers IQ and reduces efficiency. It is better to focus on one thing at a time to achieve flow." Output:
• Concept: Switching Cost
• Definition: The cognitive penalty incurred when shifting focus between tasks, resulting in lowered efficiency and intelligence.
• Actionable Takeaway: Practice single-tasking to induce flow states rather than multitasking.
Task: Extract the core ideas from the text below following the pattern above.
Input Text: <input> {{INSERT_TEXT_HERE}} </input>
            """;
    
    @Override
    public List<String> extractInformation(List<PDDocument> data) {
        // Implementation for extracting information from the provided data
        log.info("Extracting information from data: {}", data);

        // Prepare a list to hold extracted information
        List<String> res = new ArrayList<>();

        // Process each PDDocument in the input list
        for (PDDocument doc : data) {
            // Extract text content from the PDDocument
            String content = extractTextFromPDF(doc);
            log.info("Extracting information out of the following text: {}",
                    content.substring(0, Math.min(content.length(), 100)));

            // Build and send the prompt to the chat model
            ChatResponse response = chatModel.call(buildExtractionPrompt(content));
            // Retrieve and store the result
            String result = response.getResult().getOutput().getText();

            log.debug("Extracted information: {}", result);

            res.add(result);
        }
        
        log.info("Extracted information count: {}", res.size());

        return res;
    }

    private Prompt buildExtractionPrompt(String text) {
        // Construct the user message by inserting the text
        String userMessage = EXTRACT_INFO_USER_MESSAGE.replace("{{INSERT_TEXT_HERE}}", text);

        // Return the constructed prompt
        return Prompt.builder().messages(List.of(
                SystemMessage.builder().text(EXTRACT_INFO_SYSTEM_MESSAGE).build(),
                UserMessage.builder().text(userMessage).build())).build();
    }
    
    private String extractTextFromPDF(PDDocument document) {
        PDFTextStripper stripper;
        
        try {
            stripper = new PDFTextStripper();
            return stripper.getText(document);
        } catch (IOException e) {
            log.error("Failed to create PDFTextStripper", e);
            throw new RuntimeException("Failed to initialize PDF text extraction", e);
        }
    }
}
