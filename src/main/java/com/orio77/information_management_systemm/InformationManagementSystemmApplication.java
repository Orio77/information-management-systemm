package com.orio77.information_management_systemm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.orio77.information_management_systemm.extraction.InformationExtractionService;
import com.orio77.information_management_systemm.loading.DataHandlingService;
import com.orio77.information_management_systemm.ordering.InformationOrderingService;
import com.orio77.information_management_systemm.persistence.InformationPersistenceService;
import com.orio77.information_management_systemm.processing.InformationProcessingService;

@SpringBootApplication
public class InformationManagementSystemmApplication implements CommandLineRunner {

	@Autowired
	private DataHandlingService dataHandlingService;

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

		// 0. Config
		String source = "defaultSource";

		// 1. Load data
		String data = dataHandlingService.loadData(source);
		
		// 2. Extract information
		String extractedInfo = informationExtractionService.extractInformation(data);
		
		// 3. Process information
		String processedInfo = informationProcessingService.processInformation(extractedInfo);
		
		// 4. Order information
		String orderedInfo = informationOrderingService.orderInformation(processedInfo);
		
		// 5. Persist information
		informationPersistenceService.persistInformation(orderedInfo);
	}

}
