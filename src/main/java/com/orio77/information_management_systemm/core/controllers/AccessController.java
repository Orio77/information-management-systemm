package com.orio77.information_management_systemm.core.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.orio77.information_management_systemm.core.services.IdeaCreationService;
import com.orio77.information_management_systemm.extraction.Idea;
import com.orio77.information_management_systemm.extraction.IdeaRepository;
import com.orio77.information_management_systemm.loading.FileData;
import com.orio77.information_management_systemm.loading.FileDataRepository;
import com.orio77.information_management_systemm.loading.impl.DataHandlingServiceImpl;
import com.orio77.information_management_systemm.ordering.IdeaPrerequisite;
import com.orio77.information_management_systemm.ordering.IdeaPrerequisiteRepository;
import com.orio77.information_management_systemm.processing.Explanation;
import com.orio77.information_management_systemm.processing.ExplanationRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController()
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AccessController {

    private final IdeaRepository ideaRepo;

    private final IdeaPrerequisiteRepository ideaPrerequisiteRepo;

    private final ExplanationRepository explanationRepo;

    private final FileDataRepository fileDataRepo;

    private final DataHandlingServiceImpl dataHandlingService;

    private final IdeaCreationService ideaCreationService;

    @GetMapping("/ideas")
    public List<Idea> fetchAllIdeas() {
        return ideaRepo.findAll();
    }

    @GetMapping("/idea/{id}")
    public Idea fetchIdeaById(@RequestParam Long id) {
        return ideaRepo.getReferenceById(id);
    }

    @GetMapping("/prerequisites")
    public List<IdeaPrerequisite> fetchAllIdeaPrerequisites() {
        return ideaPrerequisiteRepo.findAll();
    }

    @GetMapping("/explanation")
    public List<Explanation> fetchAllExplanations() {
        return explanationRepo.findAll();
    }

    @GetMapping("/explanation/{id}")
    public Explanation fetchAllExplanations(@RequestParam Long ideaId) {
        return explanationRepo.findByIdeaId(ideaId);
    }

    @GetMapping("/pdf/all")
    public List<FileData> fetchAllFileData() {
        return fileDataRepo.findAll();
    }

    @GetMapping("/pdf/{id}")
    public FileData fetchFileDataById(@RequestParam Long id) {
        return fileDataRepo.getReferenceById(id);
    }

    @PostMapping("/pdf/delete/{id}")
    public boolean deleteFileDataById(@RequestBody Long id) {
        try {
            fileDataRepo.deleteById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @PostMapping("/pdf/upload")
    public FileData uploadPdf(@RequestParam("file") MultipartFile file) {
        FileData fileData = dataHandlingService.loadFile(file);
        return fileDataRepo.save(fileData);
    }

    @PostMapping("/pdf/process/{id}")
    public void processPdf(@RequestBody Long id) {
        ideaCreationService.createIdeas(id);
    }

}
