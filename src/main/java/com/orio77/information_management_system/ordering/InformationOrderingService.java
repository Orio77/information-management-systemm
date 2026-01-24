package com.orio77.information_management_system.ordering;

import java.util.List;

import com.orio77.information_management_system.extraction.Idea;

public interface InformationOrderingService {

    public List<IdeaPrerequisite> orderInformation(List<Idea> ideas);
}
