package com.smanager.controllers;

import com.smanager.dao.models.Assignment;
import com.smanager.dao.models.Course;
import com.smanager.dao.models.Solution;
import com.smanager.dao.models.Student;
import com.smanager.dao.repositories.AssignmentRepository;
import com.smanager.dao.repositories.CourseRepository;
import com.smanager.dao.repositories.SolutionRepository;
import com.smanager.dao.repositories.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.thymeleaf.expression.Sets;

import java.util.*;

@Controller
@RequestMapping("/Work")
public class WorkController {

    private CourseRepository courseRepository;
    private AssignmentRepository assignmentRepository;
    private SolutionRepository solutionRepository;
    private StudentRepository studentRepository;

    @Autowired
    public WorkController(CourseRepository courseRepository, AssignmentRepository assignmentRepository,
                          SolutionRepository solutionRepository, StudentRepository studentRepository) {
        this.courseRepository = courseRepository;
        this.assignmentRepository = assignmentRepository;
        this.solutionRepository = solutionRepository;
        this.studentRepository = studentRepository;
    }

    @GetMapping("/Index")
    public String index(Model model, Long studentId) {
        Student student = studentRepository.getOne(studentId);
        if (student == null) {
            return "redirect:/Index";
        }

        List<Course> courses = courseRepository.findCoursesByStudentId(studentId);
        Map<Course, List<Assignment>> courseAssignmentsMap = new HashMap<>();
        Map<Assignment, Set<Solution>> assignmentSolutionsMap = new HashMap<>();
        
        for (Course course : courses) {
            if (!courseAssignmentsMap.containsKey(course)) {
                List<Assignment> courseAssignments = assignmentRepository.findAllByCourseId(course.getId());
                courseAssignmentsMap.put(course, courseAssignments);

                for (Assignment assignment : courseAssignments) {
                    Set<Solution> assignmentSolutions = assignment.getSolutions();
                    assignmentSolutions.removeIf(s -> !s.getStudent().getId().equals(studentId));
                    if (!assignmentSolutionsMap.containsKey(assignment)) {
                        assignmentSolutionsMap.put(assignment, assignmentSolutions);
                    }
                }
            }
        }

        model.addAttribute("student", student);
        model.addAttribute("courses", courses);
        model.addAttribute("courseAssignments", courseAssignmentsMap);
        model.addAttribute("assignmentsSolutions", assignmentSolutionsMap);

        return "work_index";
    }
}
