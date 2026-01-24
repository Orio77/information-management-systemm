package com.orio77.information_management_system.processing;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ExplanationRepository extends JpaRepository<Explanation, Long> {

    Explanation findByIdeaId(Long ideaId);
}
