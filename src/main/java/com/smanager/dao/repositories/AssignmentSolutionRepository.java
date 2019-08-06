package com.smanager.dao.repositories;

import com.smanager.dao.models.Assignment;
import com.smanager.dao.models.AssignmentSolution;
import com.smanager.dao.models.Solution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

public interface AssignmentSolutionRepository extends JpaRepository<AssignmentSolution, Long> {
}
