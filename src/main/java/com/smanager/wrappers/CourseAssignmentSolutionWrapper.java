package com.smanager.wrappers;

import com.smanager.dao.models.Assignment;
import com.smanager.dao.models.Course;
import com.smanager.dao.models.Solution;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CourseAssignmentSolutionWrapper {
    private List<Course> courses;
    private Map<Course, List<Assignment>> courseAssignmentsMap;
    private Map<Assignment, List<Solution>> assignmentSolutionsMap;

    public CourseAssignmentSolutionWrapper(List<Course> courses, Map<Course, List<Assignment>> courseAssignmentsMap,
                                           Map<Assignment, List<Solution>> assignmentSolutionsMap) {
        this.courses = courses;
        this.courseAssignmentsMap = courseAssignmentsMap;
        this.assignmentSolutionsMap = assignmentSolutionsMap;
    }

    public List<Course> getCourses() {
        return courses;
    }

    public Map<Course, List<Assignment>> getCourseAssignmentsMap() {
        return courseAssignmentsMap;
    }

    public Map<Assignment, List<Solution>> getAssignmentSolutionsMap() {
        return assignmentSolutionsMap;
    }
}
