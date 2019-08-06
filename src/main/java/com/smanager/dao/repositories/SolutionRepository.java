package com.smanager.dao.repositories;

import com.smanager.dao.models.Solution;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SolutionRepository extends JpaRepository<Solution, Long> {
}
