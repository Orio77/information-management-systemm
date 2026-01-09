package com.orio77.information_management_systemm.processing;

import lombok.Data;

@Data
public class Explanation {
    private Long id;
    private String context; // optional
    private String content;
    private Long ideaId;
}
