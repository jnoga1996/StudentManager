package com.smanager.dao.repositories;

import com.smanager.dao.models.Solution;
import com.smanager.wrappers.GradeWrapper;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SolutionRepository extends JpaRepository<Solution, Long> {

    List<Solution> findAllByAssignment_Id(Long id);

    List<Solution> findAllByAssignment_IdAndFinishedTrue(Long id);

    @Query(nativeQuery = true, value = "select solution_id, grade from solutions s join assignments a on s.assignment_assignment_id = a.assignment_id join course_student cs on s.student_student_id = cs.student_id where a.assignment_course_id = :courseId group by solution_id")
    List<GradeWrapper> getAllGrades();
}
