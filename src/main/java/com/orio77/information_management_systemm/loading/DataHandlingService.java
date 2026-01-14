package com.orio77.information_management_systemm.loading;

import java.util.List;

public interface DataHandlingService {

    public List<FileData> loadData();

    public FileData loadFile();
}
