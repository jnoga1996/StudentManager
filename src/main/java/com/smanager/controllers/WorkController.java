package com.smanager.controllers;

import com.smanager.dao.models.*;
import com.smanager.dao.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private UserRepository userRepository;

    @Autowired
    public WorkController(CourseRepository courseRepository, AssignmentRepository assignmentRepository,
                          SolutionRepository solutionRepository, StudentRepository studentRepository,
                          UserRepository userRepository) {
        this.courseRepository = courseRepository;
        this.assignmentRepository = assignmentRepository;
        this.solutionRepository = solutionRepository;
        this.studentRepository = studentRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/Index")
    public String index(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        User user = userRepository.getUserByUsername(currentUsername);
        Student student = null;
        if (user != null) {
            if (user.getStudentUser() != null) {
                student = studentRepository.getOne(user.getStudentUser().getId());
            } else {
                return "redirect:/Index";
            }
        }
        Long studentId = student.getId();
        /*
        Student student = studentRepository.getOne(studentId);
        if (student == null) {
            return "redirect:/Index";
        }
         */

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
