package com.orio77.information_management_systemm.loading;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class DataHandlingServiceTest {

    @Autowired
    private DataHandlingService dataHandlingService;
    
    @Test
    public void testLoadFile() {
        PDDocument file = dataHandlingService.loadFile();
        // Assert that the file is loaded
        assert (file != null);
        // Assert that the file has pages
        assert (file.getNumberOfPages() > 0);
        // Close the document without throwing an exception
        assertDoesNotThrow(() -> file.close());
    }

    @Test
    public void testLoadData() {
        var data = dataHandlingService.loadData();
        // Assert that data is loaded
        assert (data != null);
        // Assert that at least one document is loaded
        assert (data.size() > 0);
        // Close all loaded documents without throwing exceptions
        data.forEach(doc -> assertDoesNotThrow(() -> doc.close()));
    }
}
