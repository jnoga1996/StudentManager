package com.smanager.controllers;

import com.smanager.WebSecurityConfig;
import com.smanager.dao.models.*;
import com.smanager.dao.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/Course")
public class CourseController {
    private static final String INDEX_REDIRECT_STRING = "redirect:/Course/Index";

    private CourseRepository courseRepository;
    private AssignmentRepository assignmentRepository;
    private SolutionRepository solutionRepository;
    private StudentRepository studentRepository;
    private WebSecurityConfig webSecurityConfig;

    @Autowired
    public CourseController(CourseRepository courseRepository, AssignmentRepository assignmentRepository,
                            SolutionRepository solutionRepository, StudentRepository studentRepository,
                            WebSecurityConfig webSecurityConfig) {
        this.courseRepository = courseRepository;
        this.assignmentRepository = assignmentRepository;
        this.solutionRepository = solutionRepository;
        this.studentRepository = studentRepository;
        this.webSecurityConfig = webSecurityConfig;
    }

    @GetMapping("Index")
    public String index(Model model) {
        model.addAttribute("courses", courseRepository.findAll());
        return "course_index";
    }

    @GetMapping("MyCourses")
    public String myCourses(Model model, Long studentId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        model.addAttribute("courses", courseRepository.findCoursesByStudentId(studentId));
        return "course_index";
    }

    @GetMapping("/Create")
    public String showForm(Course course, Model model) {
        model.addAttribute("isCreate", true);
        return "course_form";
    }

    @PostMapping("/Create")
    public String create(@Valid Course course, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "course_form";
        }
        courseRepository.save(course);

        return INDEX_REDIRECT_STRING;
    }

    @GetMapping("/Edit")
    public String edit(Model model, Long id) {
        Course course = courseRepository.getOne(id);
        if (course == null) {
            return INDEX_REDIRECT_STRING;
        }
        model.addAttribute("course", course);
        model.addAttribute("isCreate", false);
        model.addAttribute("id", id);
        return "course_form";
    }

    @PostMapping("/Edit")
    public String edit(@Valid Course course, Model model, BindingResult binding) {
        if (binding.hasErrors()) {
            return INDEX_REDIRECT_STRING;
        }

        Course courseFromDb = courseRepository.getOne(course.getId());
        courseFromDb.setTitle(course.getTitle());
        courseFromDb.setEcts(course.getEcts());
        courseRepository.save(courseFromDb);

        return INDEX_REDIRECT_STRING;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/Delete")
    public String delete(Long id) {
        Course course = courseRepository.getOne(id);
        if (course != null) {
            for (Assignment assignment : course.getAssignments()) {
                System.out.println(String.format("Removed assignment %s", assignment));
                assignmentRepository.delete(assignment);
                for (Solution solution : assignment.getSolutions()) {
                    System.out.println(String.format("Removed solution %s", solution));
                    solutionRepository.delete(solution);
                }
            }
            courseRepository.delete(course);
        }

        return INDEX_REDIRECT_STRING;
    }

    @GetMapping("/Register")
    public String registerStudentToCourse(Model model) {
        model.addAttribute("courses", courseRepository.findAll());
        model.addAttribute("students", studentRepository.findAll());
        model.addAttribute("mapping", new CourseStudentMapping());
        return "courseStudentRegister_form";
    }

    @PostMapping("/Register")
    public String registerStudentToCourse(@Valid CourseStudentMapping mapping, Model model) {
        Course course = courseRepository.getOne(mapping.getCourse().getId());
        Student student = studentRepository.getOne(mapping.getStudent().getId());
        Set<Student> registeredStudents = course.getStudents();

        if (registeredStudents.isEmpty()) {
            registeredStudents = new HashSet<>();
        }

        if (student != null) {
            registeredStudents.add(student);
        }

        if (course != null) {
            course.setStudents(registeredStudents);
            courseRepository.save(course);
        }

        return INDEX_REDIRECT_STRING;
    }

    @GetMapping("/RemoveFromCourse")
    public String removerStudentFromCourse(Long courseId, Long studentId) {
        Course course = courseRepository.getOne(courseId);
        Student student = studentRepository.getOne(studentId);

        if (course != null && student != null) {
            course.getStudents().remove(student);
            courseRepository.save(course);
        }

        return INDEX_REDIRECT_STRING;
    }
}
