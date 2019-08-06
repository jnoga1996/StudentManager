package com.smanager.dao.repositories;

import com.smanager.dao.models.Assignment;
import com.smanager.dao.models.AssignmentSolution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

}
