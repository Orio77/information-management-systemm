package com.orio77.information_management_systemm;

import java.util.List;

import org.springframework.ai.chat.model.Generation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.orio77.information_management_systemm.extraction.InformationExtractionService;
import com.orio77.information_management_systemm.formatting.DataFormattingService;
import com.orio77.information_management_systemm.loading.DataHandlingService;
import com.orio77.information_management_systemm.ordering.InformationOrderingService;
import com.orio77.information_management_systemm.persistence.InformationPersistenceService;
import com.orio77.information_management_systemm.processing.InformationProcessingService;

@SpringBootApplication
public class InformationManagementSystemmApplication implements CommandLineRunner {

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
		List<String> data = List.of(dataHandlingService.loadFile());

		// 2. Format data
		List<String> formattedData = dataFormattingService.formatData(data);

		// 3. Extract information
		List<List<Generation>> extractedInfo = informationExtractionService.extractInformation(formattedData);

		// TODO Implement response extraction

		// 4. Process information
		String processedInfo = informationProcessingService.processInformation(List.of());

		// 5. Order information
		String orderedInfo = informationOrderingService.orderInformation(processedInfo);

		// 6. Persist information
		informationPersistenceService.persistInformation(orderedInfo);
	}

}
