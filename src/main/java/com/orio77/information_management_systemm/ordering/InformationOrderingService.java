package com.orio77.information_management_systemm.ordering;

import java.util.List;

import com.orio77.information_management_systemm.extraction.Idea;

public interface InformationOrderingService {

    public List<IdeaPrerequisite> orderInformation(List<Idea> ideas);
}
