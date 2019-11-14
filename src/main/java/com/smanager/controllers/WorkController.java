package com.smanager.controllers;

import com.smanager.Bundles;
import com.smanager.dao.models.*;
import com.smanager.dao.repositories.*;
import com.smanager.services.UserService;
import com.smanager.storage.StorageService;
import com.smanager.utils.CourseHelper;
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

    private StudentRepository studentRepository;
    private TeacherRepository teacherRepository;
    private UserRepository userRepository;
    private UserService userService;
    private List<String> reportList;
    private CourseHelper courseHelper;
    private User user;

    @Autowired
    public WorkController(CourseRepository courseRepository, AssignmentRepository assignmentRepository,
                          StudentRepository studentRepository, UserRepository userRepository,
                          TeacherRepository teacherRepository, UserService userService) {
        this.studentRepository = studentRepository;
        this.userRepository = userRepository;
        this.teacherRepository = teacherRepository;
        this.courseHelper = new CourseHelper(courseRepository, assignmentRepository);
        this.userService = userService;
        user = userService.getLoggedUser();

        reportList = Arrays.asList(
                "/TeacherWork",
                "/NoGradeReport",
                "/NoCommentReport"
        );
    }

    @GetMapping("/Index")
    @PreAuthorize("hasRole('STUDENT')")
    public String index(Model model) {
        Student student = null;
        if (user != null) {
            if (user.getStudentUser() != null) {
                student = studentRepository.getOne(user.getStudentUser().getId());
            } else {
                return "redirect:/Index";
            }
        }
        Long studentId = student.getId();
        CourseAssignmentSolutionWrapper wrapper = courseHelper.populateCoursesAssignmentsAndSolutions(studentId, s -> s.getStudent().getId().equals(studentId));
        fillModelForStudentReports(model, student, wrapper, user);

        return "work_index";
    }

    @GetMapping("/Menu")
    public String sideMenu(Model model) {
        Long studentOrTeacherId = userService.getStudentOrTeacherId(user);
        CourseAssignmentSolutionWrapper wrapper = courseHelper.populateCoursesAssignmentsAndSolutions(studentOrTeacherId, s -> s.getId() > 0);
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
        Teacher teacher = null;
        if (user != null) {
            if (user.getTeacherUser() != null) {
                teacher = teacherRepository.getOne(user.getTeacherUser().getId());
            } else {
                return "redirect:/Index";
            }
        }
        Long teacherId = teacher.getId();
        CourseAssignmentSolutionWrapper wrapper = courseHelper.populateCoursesAssignmentsAndSolutions(teacherId, s -> s.isFinished());
        fillModelForTeacherReports(model, teacher, wrapper, user);

        return "work_index_teacher";
    }

    @GetMapping("/NoGradeReport")
    @PreAuthorize("hasRole('TEACHER')")
    public String generateReporForTeacherWithSolutionsWithoutGrade(Model model) {
        Teacher teacher = null;
        if (user != null) {
            if (user.getTeacherUser() != null) {
                teacher = teacherRepository.getOne(user.getTeacherUser().getId());
            } else {
                return "redirect:/Index";
            }
        }
        Long teacherId = teacher.getId();

        CourseAssignmentSolutionWrapper wrapper = courseHelper.populateCoursesAssignmentsAndSolutions(teacherId, s -> s.getGrade() == null);
        fillModelForTeacherReports(model, teacher, wrapper, user);

        return "work_index_teacher";
    }

    @GetMapping("/NoCommentReport")
    @PreAuthorize("hasRole('TEACHER')")
    public String generateReporForTeacherWithSolutionsWithoutComment(Model model) {
        Teacher teacher = null;
        if (user != null) {
            if (user.getTeacherUser() != null) {
                teacher = teacherRepository.getOne(user.getTeacherUser().getId());
            } else {
                return "redirect:/Index";
            }
        }
        Long teacherId = teacher.getId();
        CourseAssignmentSolutionWrapper wrapper = courseHelper.populateCoursesAssignmentsAndSolutions(teacherId, s -> s.getComments().isEmpty());
        fillModelForTeacherReports(model, teacher, wrapper, user);

        return "work_index_teacher";
    }

    @GetMapping("/GradesReport")
    @PreAuthorize("hasRole('STUDENT')")
    public String generateGradesReportForStudent(Model model) {
        Long id = userService.getStudentOrTeacherId(user);

        List<Course> courses = courseHelper.getCourseRepository().findCoursesByStudentId(id);
        Map<Course, Integer> courseGradeMap = new HashMap<>();

        for (Course course : courses) {
            int grade = CourseHelper.getGradeForCourse(course, id);
            courseGradeMap.put(course, grade);
        }

        model.addAttribute("courseGrades", courseGradeMap);
        model.addAttribute("user", user);

        return "grade_report";
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
