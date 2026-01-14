package com.orio77.information_management_systemm.loading;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class DataHandlingServiceTest {

    @Autowired
    private DataHandlingService dataHandlingService;

    @Test
    public void testLoadFile() {
        var file = dataHandlingService.loadFile();
        // Assert that the file is loaded
        assert (file != null);
        assert !(file.getContent().strip().isBlank());
    }

    @Test
    public void testLoadData() {
        var data = dataHandlingService.loadData();
        // Assert that data is loaded
        assert (data != null);
        // Assert that at least one document is loaded
        assert (data.isEmpty() == false);
    }
}
