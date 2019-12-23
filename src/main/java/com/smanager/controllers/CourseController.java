package com.smanager.controllers;

import com.smanager.dao.models.*;
import com.smanager.dao.repositories.*;
import com.smanager.services.UserService;
import com.smanager.utils.PaginationHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

@Controller
@RequestMapping("/Course")
public class CourseController {

    public static final String DETAILS_URL = "/Course/Details";
    private static final String INDEX_REDIRECT_STRING = "redirect:/Course/Index";

    private CourseRepository courseRepository;
    private AssignmentRepository assignmentRepository;
    private SolutionRepository solutionRepository;
    private StudentRepository studentRepository;
    private UserService userService;
    private PaginationHelper<Course> paginationHelper;

    private String searchValue;

    @Autowired
    public CourseController(CourseRepository courseRepository, AssignmentRepository assignmentRepository,
                            SolutionRepository solutionRepository, StudentRepository studentRepository,
                            UserService userService) {
        this.courseRepository = courseRepository;
        this.assignmentRepository = assignmentRepository;
        this.solutionRepository = solutionRepository;
        this.studentRepository = studentRepository;
        this.userService = userService;
        paginationHelper = new PaginationHelper<>(courseRepository);
    }

    @GetMapping("Index")
    public String index(Model model) {
        User user = userService.getLoggedUser();
        List<Course> courses;
        if (searchValue != null && !searchValue.isEmpty()) {
            courses = courseRepository.findCourseByTitleContaining(searchValue);
        } else {
            courses = courseRepository.findAll();
        }
        fillModel(model, courses, user);
        this.searchValue = "";

        return "course_index";
    }

    @GetMapping("Index/{page}")
    public String index(Model model, @PathVariable("page") int page) {
        User user = userService.getLoggedUser();
        Page<Course> courses = paginationHelper.getPage(page);
        fillModel(model, courses, user);

        return "course_index";
    }

    @PostMapping("Search")
    public String search(@RequestParam("search") String searchValue) {
        this.searchValue = searchValue.toLowerCase().trim();

        return INDEX_REDIRECT_STRING;
    }

    @GetMapping("TeacherIndex")
    public String teacherIndex(Model model) {
        User user = userService.getLoggedUser();
        Long teacherId = userService.getStudentOrTeacherId(user);
        List<Course> courses = courseRepository.findCoursesByTeacherId(teacherId);

        model.addAttribute("courses", courses);
        model.addAttribute("user", user);

        return "course_index";
    }

    @GetMapping("MyCourses")
    public String myCourses(Model model, Long studentId) {
        User user = userService.getLoggedUser();
        model.addAttribute("courses", courseRepository.findCoursesByStudentId(studentId));
        model.addAttribute("user", user);

        return "course_index";
    }

    @GetMapping("/Create")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public String showForm(Course course, Model model) {
        User user = userService.getLoggedUser();
        model.addAttribute("isCreate", true);
        model.addAttribute("user", user);

        return "course_form";
    }

    @PostMapping("/Create")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public String create(@Valid Course course, BindingResult bindingResult, Model model) {
        User user = userService.getLoggedUser();
        if (bindingResult.hasErrors()) {
            return "course_form";
        }
        courseRepository.save(course);
        model.addAttribute("user", user);

        return INDEX_REDIRECT_STRING;
    }

    @GetMapping("/Edit")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public String edit(Model model, Long id) {
        User user = userService.getLoggedUser();
        Course course = courseRepository.getOne(id);
        if (course == null) {
            return INDEX_REDIRECT_STRING;
        }
        model.addAttribute("course", course);
        model.addAttribute("isCreate", false);
        model.addAttribute("id", id);
        model.addAttribute("user", user);

        return "course_form";
    }

    @PostMapping("/Edit")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public String edit(@Valid Course course, Model model, BindingResult binding) {
        User user = userService.getLoggedUser();
        if (binding.hasErrors()) {
            return INDEX_REDIRECT_STRING;
        }

        Course courseFromDb = courseRepository.getOne(course.getId());
        courseFromDb.setTitle(course.getTitle());
        courseFromDb.setEcts(course.getEcts());
        courseRepository.save(courseFromDb);

        model.addAttribute("user", user);

        return INDEX_REDIRECT_STRING;
    }

    @GetMapping("/Details")
    public String getDetails(Model model, Long id) {
        User user = userService.getLoggedUser();
        Course course = courseRepository.getOne(id);
        if (course == null) {
            return INDEX_REDIRECT_STRING;
        }
        model.addAttribute("course", course);
        model.addAttribute("user", user);

        return "course_details";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/Delete")
    public String delete(Model model, Long id) {
        User user = userService.getLoggedUser();
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
        model.addAttribute("user", user);

        return INDEX_REDIRECT_STRING;
    }

    @GetMapping("/Register")
    public String registerStudentToCourse(Model model) {
        User user = userService.getLoggedUser();
        model.addAttribute("courses", courseRepository.findAll());
        model.addAttribute("students", studentRepository.findAll());
        model.addAttribute("mapping", new CourseStudentMapping());
        model.addAttribute("user", user);

        return "courseStudentRegister_form";
    }

    @PostMapping("/Register")
    public String registerStudentToCourse(@Valid CourseStudentMapping mapping, Model model) {
        User user = userService.getLoggedUser();
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
        model.addAttribute("user", user);

        return INDEX_REDIRECT_STRING;
    }

    @GetMapping("/RemoveFromCourse")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public String removerStudentFromCourse(Long courseId, Long studentId, Model model) {
        User user = userService.getLoggedUser();
        Course course = courseRepository.getOne(courseId);
        Student student = studentRepository.getOne(studentId);

        if (course != null && student != null) {
            course.getStudents().remove(student);
            courseRepository.save(course);
        }
        model.addAttribute("user", user);

        return INDEX_REDIRECT_STRING;
    }

    private void fillModel(Model model, Iterable<Course> courses, User user) {
        model.addAttribute("courses", courses);
        model.addAttribute("user", user);
        model.addAttribute("pages", paginationHelper.getPageList());
    }
}
