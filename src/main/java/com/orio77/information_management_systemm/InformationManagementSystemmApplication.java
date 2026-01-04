package com.orio77.information_management_systemm;

import com.orio77.information_management_systemm.extraction.InformationExtractionService;
import com.orio77.information_management_systemm.formatting.DataFormattingService;
import com.orio77.information_management_systemm.loading.DataHandlingService;
import com.orio77.information_management_systemm.ordering.InformationOrderingService;
import com.orio77.information_management_systemm.persistence.InformationPersistenceService;
import com.orio77.information_management_systemm.processing.InformationProcessingService;
import com.orio77.information_management_systemm.util.AIUtil;

import java.io.IOException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.ai.chat.model.Generation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class InformationManagementSystemmApplication
    implements CommandLineRunner {

  @Autowired
  private DataHandlingService dataHandlingService;

  @Autowired
  private DataFormattingService dataFormattingService;

  @Autowired
  private InformationExtractionService informationExtractionService;

  @Autowired
  private InformationProcessingService informationProcessingService;

  @Autowired
  private InformationPersistenceService informationPersistenceService;

  @Autowired
  private InformationOrderingService informationOrderingService;

  public static void main(String[] args) {
    SpringApplication.run(InformationManagementSystemmApplication.class, args);
  }

  @Override
  public void run(String... args) throws Exception {
    System.out.println("Information Management System is running...");

    // 1. Load data
    List<PDDocument> data = List.of(dataHandlingService.loadFile());

    // 2. Format data
    List<PDDocument> formattedData = dataFormattingService.formatData(data);

    String sourceContent = extractTextFromPDF(formattedData.get(0));

    // 3. Extract information
    List<List<Generation>> extractedInfo = informationExtractionService.extractInformation(List.of(sourceContent));

    // 3.5 Parse Responses
    // Parse response
    List<String> parsedInfo = AIUtil.extractJSONFromResponse(extractedInfo.get(1));

    // 4. Process information
    List<List<Generation>> processedInfo = informationProcessingService.processInformation(
        parsedInfo, sourceContent);

    // 5. Order information
    String orderedInfo = informationOrderingService.orderInformation();

    // 6. Persist information
    informationPersistenceService.persistInformation(orderedInfo);
  }

  private static String extractTextFromPDF(PDDocument document) {
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
