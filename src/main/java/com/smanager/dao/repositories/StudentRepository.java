package com.smanager.dao.repositories;

import com.smanager.dao.models.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    @Query(nativeQuery = true, value = "select * from students s join course_student cs on s.student_id = cs.student_id where cs.course_id = :courseId")
    List<Student> findAllByCoursesIn(Long courseId);
}
