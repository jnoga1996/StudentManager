package com.smanager.dao.repositories;

import com.smanager.dao.models.FileHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileHistoryRepository extends JpaRepository<FileHistory, Long> {
}
