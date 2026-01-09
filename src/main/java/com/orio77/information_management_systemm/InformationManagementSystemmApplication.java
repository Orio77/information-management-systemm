package com.orio77.information_management_systemm;

import java.util.List;

import org.springframework.ai.chat.model.Generation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.orio77.information_management_systemm.extraction.Idea;
import com.orio77.information_management_systemm.extraction.InformationExtractionService;
import com.orio77.information_management_systemm.extraction.impl.IdeaFactoryService;
import com.orio77.information_management_systemm.formatting.DataFormattingService;
import com.orio77.information_management_systemm.loading.DataHandlingService;
import com.orio77.information_management_systemm.ordering.InformationOrderingService;
import com.orio77.information_management_systemm.persistence.InformationPersistenceService;
import com.orio77.information_management_systemm.processing.Explanation;
import com.orio77.information_management_systemm.processing.InformationProcessingService;
import com.orio77.information_management_systemm.processing.impl.ExplanationFactoryService;

@SpringBootApplication
public class InformationManagementSystemmApplication implements CommandLineRunner {

	@Autowired
	private DataHandlingService dataHandlingService;

	@Autowired
	private DataFormattingService dataFormattingService;

	@Autowired
	private InformationExtractionService informationExtractionService;

	@Autowired
	private IdeaFactoryService ideaFactoryService;

	@Autowired
	private InformationProcessingService informationProcessingService;

	@Autowired
	private ExplanationFactoryService explanationFactoryService;

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
		String data = dataHandlingService.loadFile();

		// 2. Format data
		data = dataFormattingService.formatData(data);

		// 3. Extract information
		List<Generation> extractedInfo = informationExtractionService.extractInformation(data);

		// Extract Response
		List<Idea> ideas = ideaFactoryService.createIdeasFromGenerations(extractedInfo, 1L);

		// 4. Process information
		List<Generation> processedInfo = informationProcessingService.processInformation(
				ideas.get(0).getContent(),
				data);

		List<Explanation> explanations = explanationFactoryService.createExplanationsFromGenerations(processedInfo,
				ideas.get(0).getId());

		// 5. Order information
		String orderedInfo = informationOrderingService.orderInformation("");

		// 6. Persist information
		informationPersistenceService.persistInformation(orderedInfo);
	}

}
