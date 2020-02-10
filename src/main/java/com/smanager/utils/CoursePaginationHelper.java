package com.smanager.utils;

import com.smanager.dao.models.Course;
import com.smanager.dao.models.User;
import com.smanager.dao.repositories.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public class CoursePaginationHelper extends PaginationHelper<Course> {

    private CourseRepository courseRepository;

    @Autowired
    public CoursePaginationHelper(JpaRepository repository, CourseRepository courseRepository) {
        super(repository);
        this.courseRepository = courseRepository;
    }

    public CoursePaginationHelper(CourseRepository courseRepository) {
        super(courseRepository);
        this.courseRepository = courseRepository;
    }

    public Page<Course> getPage(int index, User user) {
        List<Course> userCourses = getCourses(user);
        int count = userCourses.size();
        int availablePages = count / PAGE_SIZE;
        Pageable page;
        if (index > availablePages) {
            page = PageRequest.of(1, PAGE_SIZE);
        } else {
            page = PageRequest.of(index, PAGE_SIZE);
        }

        return getCourses(user, page);
    }

    public List<Course> getCourses(User user) {
        List<Course> courses;
        if (user != null && user.isStudent()) {
            courses = courseRepository.findCoursesByStudentId(user.getStudentUser().getId());
        } else if (user != null && user.isTeacher()) {
            courses = courseRepository.findCoursesByTeacherId(user.getTeacherUser().getId());
        } else {
            courses = courseRepository.findAll();
        }
        return courses;
    }

    public Page<Course> getCourses(User user, Pageable pageable) {
        Page<Course> courses;
        if (user != null && user.isStudent()) {
            courses = courseRepository.findCoursesByStudentId(user.getStudentUser().getId(), pageable);
        } else if (user != null && user.isTeacher()) {
            courses = courseRepository.findCoursesByTeacherId(user.getTeacherUser().getId(), pageable);
        } else {
            courses = courseRepository.findAll(pageable);
        }
        return courses;
    }
}
