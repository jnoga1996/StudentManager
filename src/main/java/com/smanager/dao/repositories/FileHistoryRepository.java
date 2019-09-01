package com.smanager.dao.repositories;

import com.smanager.dao.models.FileHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FileHistoryRepository extends JpaRepository<FileHistory, Long> {
    @Query(value = "select * from file_history where id = ", nativeQuery = true)
    List<FileHistory> findAllById(@Param("id")Long id);
}
