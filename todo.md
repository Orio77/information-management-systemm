 - [ ] create a WIP branch and commit current changes there
 - [ ] add TextStripper to DataHandlingServiceImpl.java
 - [ ] make information extraction service implement Java 8 streams 
 - [ ] make information processing service implement Java 8 streams 
 - [ ] create tests for generation services with mocks
 - [ ] create tests for generation services by saving the output from the prior called service to the test resources
 - [ ] read the file manually in beforeeach or beforeall like the following:
 ```java
InputStream inputStream = getClass()
        .getResourceAsStream("/data/test-file.txt");
    String content = new String(inputStream.readAllBytes());
 ```

 - [ ] consider using langfuse for observability