package com.smanager.utils;

import com.smanager.dao.models.Assignment;
import com.smanager.dao.models.Course;
import com.smanager.dao.models.Solution;
import com.smanager.dao.repositories.AssignmentRepository;
import com.smanager.dao.repositories.CourseRepository;
import com.smanager.wrappers.CourseAssignmentSolutionWrapper;

import java.util.ArrayList;
import java.util.HashMap;
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

    public CourseRepository getCourseRepository() {
        return this.courseRepository;
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

    public static int getGradeForAssignment(Assignment assignment, Long studentId) {
        double sum = 0;
        List<Solution> solutions = assignment.getSolutions()
                .stream()
                .filter(s -> s.getStudent().getId() == studentId && s.isFinished())
                .collect(Collectors.toList());

        for (Solution solution : assignment.getSolutions()) {
            sum += solution.getGrade();
        }

        int grade = 0;
        try {
            grade = (int)(sum / solutions.size());
        } catch (ArithmeticException ex) {
            grade = -1;
        }

        return grade;
    }

    public static List<Integer> getGradesForAssignment(Assignment assignment, Long studentId) {
        List<Integer> grades = new ArrayList<>();
        List<Solution> solutions = assignment.getSolutions()
                .stream()
                .filter(s -> s.getStudent().getId() == studentId && s.isFinished())
                .collect(Collectors.toList());

        for (Solution solution : solutions) {
            grades.add(solution.getGrade().intValue());
        }

        return grades;
    }

    public static List<Integer> getGradesForCourse(Course course, Long studentId) {
        List<Integer> grades = new ArrayList<>();
        List<Assignment> assignments = course.getAssignments()
                .stream()
                .filter(c -> c.getCourse().getId() == course.getId())
                .collect(Collectors.toList());

        for (Assignment assignment : assignments) {
            List<Integer> gradesForAssignment = CourseHelper.getGradesForAssignment(assignment, studentId);
            grades.addAll(gradesForAssignment);
        }

        return grades;
    }

    public static int getGradeForCourse(Course course, Long studentId) {
        double sum = 0;
        List<Assignment> assignments = course.getAssignments()
                .stream()
                .filter(s -> s.getCourse().getId() == course.getId())
                .collect(Collectors.toList());

        for (Assignment assignment : assignments) {
            sum += getGradeForAssignment(assignment, studentId);
        }

        int grade = 0;
        try {
            grade = (int)(sum / assignments.size());
        } catch (ArithmeticException ex) {
            grade = 0;
        }

        return grade;
    }
}
