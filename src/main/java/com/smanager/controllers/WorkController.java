package com.smanager.controllers;

import com.smanager.Bundles;
import com.smanager.dao.models.*;
import com.smanager.dao.repositories.*;
import com.smanager.services.UserService;
import com.smanager.storage.StorageService;
import com.smanager.wrappers.CourseAssignmentSolutionWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/Work")
public class WorkController {

    private CourseRepository courseRepository;
    private AssignmentRepository assignmentRepository;
    private SolutionRepository solutionRepository;
    private StudentRepository studentRepository;
    private TeacherRepository teacherRepository;
    private UserRepository userRepository;
    private CommentRepository commentRepository;
    private UserService userService;
    private List<String> reportList;

    @Autowired
    public WorkController(CourseRepository courseRepository, AssignmentRepository assignmentRepository,
                          SolutionRepository solutionRepository, StudentRepository studentRepository,
                          CommentRepository commentRepository, UserRepository userRepository,
                          TeacherRepository teacherRepository) {
        this.courseRepository = courseRepository;
        this.assignmentRepository = assignmentRepository;
        this.solutionRepository = solutionRepository;
        this.studentRepository = studentRepository;
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.teacherRepository = teacherRepository;

        reportList = Arrays.asList(
                "/TeacherWork",
                "/NoGradeReport",
                "/NoCommentReport"
        );
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
        CourseAssignmentSolutionWrapper wrapper = populateCoursesAssignmentsAndSolutions(studentId, s -> s.getStudent().getId().equals(studentId));
        fillModelForStudentReports(model, student, wrapper, user);

        return "work_index";
    }

    @GetMapping("/Menu")
    public String sideMenu(Model model) {
        User user = userService.getLoggedUser();
        Long studentOrTeacherId = userService.getStudentOrTeacherId(user);
        CourseAssignmentSolutionWrapper wrapper = populateCoursesAssignmentsAndSolutions(studentOrTeacherId, s -> s.getId() > 0);
        Teacher teacher = null;
        if (user != null) {
            if (user.getTeacherUser() != null) {
                teacher = teacherRepository.getOne(user.getTeacherUser().getId());
            } else {
                return "redirect:/Index";
            }
        }
        fillModelForTeacherReports(model, teacher, wrapper, user);

        return "side_menu";
    }

    @GetMapping("/TeacherWork")
    @PreAuthorize("hasRole('TEACHER')")
    public String teacherWork(Model model) {
        userService = new UserService(SecurityContextHolder.getContext().getAuthentication(), userRepository);
        User user = userService.getLoggedUser();
        Teacher teacher = null;
        if (user != null) {
            if (user.getTeacherUser() != null) {
                teacher = teacherRepository.getOne(user.getTeacherUser().getId());
            } else {
                return "redirect:/Index";
            }
        }
        Long teacherId = teacher.getId();
        CourseAssignmentSolutionWrapper wrapper = populateCoursesAssignmentsAndSolutions(teacherId, s -> s.isFinished());
        fillModelForTeacherReports(model, teacher, wrapper, user);

        return "work_index_teacher";
    }

    @GetMapping("/NoGradeReport")
    @PreAuthorize("hasRole('TEACHER')")
    public String generateReporForTeacherWithSolutionsWithoutGrade(Model model) {
        userService = new UserService(SecurityContextHolder.getContext().getAuthentication(), userRepository);
        User user = userService.getLoggedUser();
        Teacher teacher = null;
        if (user != null) {
            if (user.getTeacherUser() != null) {
                teacher = teacherRepository.getOne(user.getTeacherUser().getId());
            } else {
                return "redirect:/Index";
            }
        }
        Long teacherId = teacher.getId();

        CourseAssignmentSolutionWrapper wrapper = populateCoursesAssignmentsAndSolutions(teacherId, s -> s.getGrade() == null);
        fillModelForTeacherReports(model, teacher, wrapper, user);

        return "work_index_teacher";
    }

    @GetMapping("/NoCommentReport")
    @PreAuthorize("hasRole('TEACHER')")
    public String generateReporForTeacherWithSolutionsWithoutComment(Model model) {
        userService = new UserService(SecurityContextHolder.getContext().getAuthentication(), userRepository);
        User user = userService.getLoggedUser();
        Teacher teacher = null;
        if (user != null) {
            if (user.getTeacherUser() != null) {
                teacher = teacherRepository.getOne(user.getTeacherUser().getId());
            } else {
                return "redirect:/Index";
            }
        }
        Long teacherId = teacher.getId();
        CourseAssignmentSolutionWrapper wrapper = populateCoursesAssignmentsAndSolutions(teacherId, s -> s.getComments().isEmpty());
        fillModelForTeacherReports(model, teacher, wrapper, user);

        return "work_index_teacher";
    }

    private CourseAssignmentSolutionWrapper populateCoursesAssignmentsAndSolutions(Long id, Predicate<Solution> predicate) {
        List<Course> courses = courseRepository.findCoursesByTeacherId(id);
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

    private static List<Solution> filterSolutions(List<Solution> solutions, Predicate<Solution> predicate) {
        return solutions.stream().filter(predicate).collect(Collectors.toList());
    }

    private void fillModelForTeacherReports(Model model, Teacher teacher, CourseAssignmentSolutionWrapper wrapper,
                                     User user) {
        fillModelForReports(model, wrapper, user);
        model.addAttribute("teacher", teacher);
    }

    private void fillModelForStudentReports(Model model, Student student, CourseAssignmentSolutionWrapper wrapper,
                                            User user) {
        fillModelForReports(model, wrapper, user);
        model.addAttribute("student", student);
    }

    private void fillModelForReports(Model model, CourseAssignmentSolutionWrapper wrapper, User user) {
        model.addAttribute("courses", wrapper.getCourses());
        model.addAttribute("courseAssignments", wrapper.getCourseAssignmentsMap());
        model.addAttribute("assignmentsSolutions", wrapper.getAssignmentSolutionsMap());
        model.addAttribute("user", user);
        model.addAttribute("reports", reportList);
    }

}
