package com.smanager.dao.repositories;

import com.smanager.dao.models.Solution;
import com.smanager.wrappers.GradeWrapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;

import java.util.List;

public interface SolutionRepository extends JpaRepository<Solution, Long> {

    List<Solution> findAllByAssignment_Id(Long id);

    List<Solution> findAllByAssignment_IdAndFinishedTrue(Long id);

    @Query(value = "select * from solutions where student_student_id = :studentId", nativeQuery = true)
    List<Solution> findAllStudentSolutions(@Param("studentId") Long studentId);

    @Query(value = "select * from solutions s join assignments a on s.assignment_assignment_id = a.assignment_id where teacher_teacher_id = :teacherId",
            nativeQuery = true)
    List<Solution> findAllStudentSolutionsForTeacher(@Param("teacherId") Long teacherId);

    @Query(value = "select * from solutions where student_student_id = :studentId", nativeQuery = true)
    Page<Solution> findAllStudentSolutions(@Param("studentId") Long studentId, Pageable pageable);

    @Query(value = "select * from solutions s join assignments a on s.assignment_assignment_id = a.assignment_id where teacher_teacher_id = :teacherId",
        nativeQuery = true)
    Page<Solution> findAllStudentSolutionsForTeacher(@Param("teacherId") Long teacherId, Pageable pageable);
}
