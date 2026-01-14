package com.orio77.information_management_systemm;

import java.util.List;

import org.springframework.ai.chat.model.Generation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.orio77.information_management_systemm.extraction.Idea;
import com.orio77.information_management_systemm.extraction.IdeaRepository;
import com.orio77.information_management_systemm.extraction.InformationExtractionService;
import com.orio77.information_management_systemm.extraction.impl.IdeaFactoryService;
import com.orio77.information_management_systemm.formatting.DataFormattingService;
import com.orio77.information_management_systemm.loading.DataHandlingService;
import com.orio77.information_management_systemm.loading.FileData;
import com.orio77.information_management_systemm.loading.FileDataRepository;
import com.orio77.information_management_systemm.ordering.InformationOrderingService;
import com.orio77.information_management_systemm.processing.Explanation;
import com.orio77.information_management_systemm.processing.ExplanationRepository;
import com.orio77.information_management_systemm.processing.InformationProcessingService;
import com.orio77.information_management_systemm.processing.impl.ExplanationFactoryService;

@SpringBootApplication
public class InformationManagementSystemmApplication implements CommandLineRunner {

	@Autowired
	private DataHandlingService dataHandlingService;

	@Autowired
	private FileDataRepository fileDataRepo;

	@Autowired
	private DataFormattingService dataFormattingService;

	@Autowired
	private InformationExtractionService informationExtractionService;

	@Autowired
	private IdeaFactoryService ideaFactoryService;

	@Autowired
	private IdeaRepository ideaRepo;

	@Autowired
	private InformationProcessingService informationProcessingService;

	@Autowired
	private ExplanationFactoryService explanationFactoryService;

	@Autowired
	private ExplanationRepository explanationRepo;

	@Autowired
	private InformationOrderingService informationOrderingService;

	public static void main(String[] args) {
		SpringApplication.run(InformationManagementSystemmApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		// 1. Load data
		FileData data = dataHandlingService.loadFile();

		boolean isProcessed = fileDataRepo.existsByTitle(data.getTitle());

		if (!isProcessed) {
			data = fileDataRepo.save(data);

			// 2. Format data
			String formattedData = dataFormattingService.formatData(data.getContent());

			// 3. Extract information
			List<Generation> extractedInfo = informationExtractionService.extractInformation(formattedData);

			// Extract Response
			List<Idea> ideas = ideaFactoryService.createIdeasFromGenerations(extractedInfo, data.getId());

			ideas = ideaRepo.saveAll(ideas);
			System.out.println("IDea:");
			System.out.println(ideas.getFirst());

			for (Idea idea : ideas) {
				// 4. Process information
				List<Generation> processedInfo = informationProcessingService.processInformation(
						idea.getContent(),
						formattedData);

				List<Explanation> explanations = explanationFactoryService.createExplanationsFromGenerations(
						processedInfo,
						idea.getId());

				explanations = explanationRepo.saveAll(explanations);
			}

			System.out.println(ideaRepo.findAll());
			System.out.println(explanationRepo.findAll());

			// 5. Order information
			String orderedInfo = informationOrderingService.orderInformation("");
		} else {
			System.out.println("Processed, not processing");
		}

	}

}
