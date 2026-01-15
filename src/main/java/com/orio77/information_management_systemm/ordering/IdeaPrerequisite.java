package com.orio77.information_management_systemm.ordering;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@IdClass(IdeaPrerequisite.IdeaPrerequisiteId.class)
@AllArgsConstructor
@NoArgsConstructor
public class IdeaPrerequisite {
    @Id
    private Long prereqIdeaId;
    @Id
    private Long ideaId;

    @Data
    public static class IdeaPrerequisiteId implements Serializable {
        private Long prereqIdeaId;
        private Long ideaId;
    }
}
