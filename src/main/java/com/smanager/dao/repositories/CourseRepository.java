package com.smanager.dao.repositories;

import com.smanager.dao.models.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {

    @Query(value = "select * from courses c " +
            "inner join course_student cs on c.course_id = cs.course_id " +
            "where cs.student_id = :student_id", nativeQuery = true)
    List<Course> findCoursesByStudentId(@Param("student_id") Long studentId);

    @Query(value = "select * from courses c " +
            "inner join course_teacher ct on c.course_id = ct.course_id " +
            "where ct.teacher_id = :teacher_id", nativeQuery = true)
    List<Course> findCoursesByTeacherId(@Param("teacher_id") Long teacherId);

    List<Course> findCourseByTitleContaining(String title);
}
