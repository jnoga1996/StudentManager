package com.smanager.dao.repositories;

import com.smanager.dao.models.Solution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SolutionRepository extends JpaRepository<Solution, Long> {
    @Query(value = "select * from solutions s " +
            "join assignment_solutions ass on s.solution_id = ass.solution_id " +
            "where assignment_id = id", nativeQuery = true)
    List<Solution> getAllByAssignmentId(@Param("id")Long id);
}
