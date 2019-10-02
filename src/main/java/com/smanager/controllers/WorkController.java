package com.smanager.controllers;

import com.smanager.dao.models.*;
import com.smanager.dao.repositories.*;
import com.smanager.services.UserService;
import com.smanager.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.thymeleaf.expression.Sets;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/Work")
public class WorkController {

    private CourseRepository courseRepository;
    private AssignmentRepository assignmentRepository;
    private SolutionRepository solutionRepository;
    private StudentRepository studentRepository;
    private UserRepository userRepository;
    private CommentRepository commentRepository;
    private UserService userService;
    private StorageService storageService;

    @Autowired
    public WorkController(CourseRepository courseRepository, AssignmentRepository assignmentRepository,
                          SolutionRepository solutionRepository, StudentRepository studentRepository,
                          CommentRepository commentRepository, UserRepository userRepository,
                          StorageService storageService) {
        this.courseRepository = courseRepository;
        this.assignmentRepository = assignmentRepository;
        this.solutionRepository = solutionRepository;
        this.studentRepository = studentRepository;
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.storageService = storageService;
    }

    @GetMapping("/Index")
    @PreAuthorize("hasRole('STUDENT')")
    public String index(Model model) {
        userService = new UserService(SecurityContextHolder.getContext().getAuthentication(), userRepository);
        User user = userService.getLoggedUser();
        Student student = null;
        if (user != null) {
            if (user.getStudentUser() != null) {
                student = studentRepository.getOne(user.getStudentUser().getId());
            } else {
                return "redirect:/Index";
            }
        }
        Long studentId = student.getId();
        List<Course> courses = courseRepository.findCoursesByStudentId(studentId);
        Map<Course, List<Assignment>> courseAssignmentsMap = new HashMap<>();
        Map<Assignment, List<Solution>> assignmentSolutionsMap = new HashMap<>();
        Map<Solution, List<Comment>> solutionCommentsMap = new HashMap<>();
        
        for (Course course : courses) {
            if (!courseAssignmentsMap.containsKey(course)) {
                List<Assignment> courseAssignments = assignmentRepository.findAllByCourseId(course.getId());
                courseAssignmentsMap.put(course, courseAssignments);

                for (Assignment assignment : courseAssignments) {
                    List<Solution> assignmentSolutions = assignment.getSolutions();
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
