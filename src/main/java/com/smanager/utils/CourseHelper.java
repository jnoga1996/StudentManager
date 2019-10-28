package com.smanager.utils;

import com.smanager.dao.models.Assignment;
import com.smanager.dao.models.Course;
import com.smanager.dao.models.Solution;
import com.smanager.dao.repositories.AssignmentRepository;
import com.smanager.dao.repositories.CourseRepository;
import com.smanager.wrappers.CourseAssignmentSolutionWrapper;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CourseHelper {

    private CourseRepository courseRepository;
    private AssignmentRepository assignmentRepository;

    public CourseHelper(CourseRepository courseRepository, AssignmentRepository assignmentRepository) {
        this.courseRepository = courseRepository;
        this.assignmentRepository = assignmentRepository;
    }

    public CourseAssignmentSolutionWrapper populateCoursesAssignmentsAndSolutions(Long id, Predicate<Solution> predicate) {
        List<Course> courses;

        if (id == null) {
            courses = courseRepository.findAll();
        } else {
            courses = courseRepository.findCoursesByTeacherId(id);
        }
        Map<Course, List<Assignment>> courseAssignmentsMap = new HashMap<>();
        Map<Assignment, List<Solution>> assignmentSolutionsMap = new HashMap<>();

        for (Course course : courses) {
            if (!courseAssignmentsMap.containsKey(course)) {
                List<Assignment> courseAssignments = assignmentRepository.findAllByCourseId(course.getId());
                courseAssignmentsMap.put(course, courseAssignments);

                for (Assignment assignment : courseAssignments) {
                    List<Solution> assignmentSolutions = assignment.getSolutions();

                    assignmentSolutions = filterSolutions(assignmentSolutions, predicate);

                    if (!assignmentSolutionsMap.containsKey(assignment)) {
                        assignmentSolutionsMap.put(assignment, assignmentSolutions);
                    }
                }
            }
        }

        return new CourseAssignmentSolutionWrapper(courses, courseAssignmentsMap, assignmentSolutionsMap);
    }

    public static List<Solution> filterSolutions(List<Solution> solutions, Predicate<Solution> predicate) {
        return solutions.stream().filter(predicate).collect(Collectors.toList());
    }
}
