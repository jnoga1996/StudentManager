package com.smanager.dao.repositories;

import com.smanager.dao.models.Assignment;
import com.smanager.dao.models.AssignmentSolution;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

}
