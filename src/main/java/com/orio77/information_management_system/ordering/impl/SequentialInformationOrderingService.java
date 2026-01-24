package com.orio77.information_management_system.ordering.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.orio77.information_management_system.extraction.Idea;
import com.orio77.information_management_system.ordering.IdeaPrerequisite;
import com.orio77.information_management_system.ordering.InformationOrderingService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SequentialInformationOrderingService implements InformationOrderingService {

    @Override
    public List<IdeaPrerequisite> orderInformation(List<Idea> information) {
        List<IdeaPrerequisite> prerequisites = new ArrayList<>();

        for (int i = 0; i < information.size() - 1; i++) {
            Long prereqId = information.get(i).getId();
            Long ideaId = information.get(i + 1).getId();
            prerequisites.add(new IdeaPrerequisite(prereqId, ideaId));
        }

        return prerequisites;
    }
}
