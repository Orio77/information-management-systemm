package com.orio77.information_management_system.loading;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FileDataRepository extends JpaRepository<FileData, Long> {

    public boolean existsByTitle(String title);
}
