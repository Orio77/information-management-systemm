package com.orio77.information_management_systemm.loading;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface DataHandlingService {

    public List<FileData> loadData();

    public FileData loadFile();

    public FileData loadFile(String filePath);

    public FileData loadFile(MultipartFile file);
}
