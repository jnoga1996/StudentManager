package com.smanager.controllers;

import com.smanager.dao.models.*;
import com.smanager.dao.repositories.*;
import com.smanager.interfaces.IUserService;
import com.smanager.utils.CoursePaginationHelper;
import com.smanager.utils.PaginationHelper;
import com.smanager.wrappers.RegisterUnregisterWrapper;
import com.smanager.wrappers.StudentRegisterWrapper;
import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
    private TeacherRepository teacherRepository;
    private IUserService userService;
    private CoursePaginationHelper paginationHelper;

    private String searchValue;

    @Autowired
    public CourseController(CourseRepository courseRepository, AssignmentRepository assignmentRepository,
                            SolutionRepository solutionRepository, StudentRepository studentRepository,
                            TeacherRepository teacherRepository, IUserService userService) {
        this.courseRepository = courseRepository;
        this.assignmentRepository = assignmentRepository;
        this.solutionRepository = solutionRepository;
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
        this.userService = userService;
        paginationHelper = new CoursePaginationHelper(courseRepository);
    }

    @GetMapping("Index")
    public String index(Model model) {
        User user = userService.getLoggedUser();
        Iterable<Course> courses;
        if (searchValue != null && !searchValue.isEmpty()) {
            courses = courseRepository.findCourseByTitleContaining(searchValue);
        } else {
            courses = paginationHelper.getPage(0, user);
        }
        fillModel(model, courses, user);
        this.searchValue = "";

        return "course_index";
    }

    @GetMapping("Index/{page}")
    public String index(Model model, @PathVariable("page") int page) {
        User user = userService.getLoggedUser();
        Page<Course> courses = paginationHelper.getPage(page, user);
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
    public String create(@Valid Course course, BindingResult bindingResult, Model model,
                         @RequestParam(name = "saveToCourse", required = false) boolean saveToCourse) {
        User user = userService.getLoggedUser();
        if (bindingResult.hasErrors()) {
            return "course_form";
        }
        courseRepository.save(course);
        model.addAttribute("user", user);
        if (saveToCourse) {
            CourseTeacherMapping mapping = new CourseTeacherMapping();
            mapping.setTeacher(user.getTeacherUser());
            mapping.setCourse(course);
            registerTeacher(mapping);
        }

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

    @GetMapping("/RegisterNew")
    public String registerStudentsToCourse(Model model, Long courseId) {
        if (courseId == null) {
            return INDEX_REDIRECT_STRING;
        }
        User user = userService.getLoggedUser();
        Course course = courseRepository.getOne(courseId);
        List<Student> students = studentRepository.findStudentsNotYetRegisteredForCourse(courseId);
        List<Student> registeredStudents = studentRepository.findAllByCoursesIn(courseId);
        List<Long> studentIdsList = new ArrayList<>();
        List<Boolean> registeredList = new ArrayList<>();
        List<Long> registeredStudentIdsList = new ArrayList<>();
        List<Boolean> unregisteredList = new ArrayList<>();
        StudentRegisterWrapper wrapper = prepareWrapper(course, students, studentIdsList, registeredList, registeredStudents, registeredStudentIdsList, unregisteredList);

        model.addAttribute("user", user);
        model.addAttribute("course", course);
        model.addAttribute("students", students);
        model.addAttribute("registeredStudents", registeredStudents);
        model.addAttribute("wrapper", wrapper);

        return "register_students";
    }

    private StudentRegisterWrapper prepareWrapper(Course course, List<Student> students, List<Long> studentIdsList,
                                                  List<Boolean> registeredList, List<Student> registeredStudents,
                                                  List<Long> registeredStudentIdsList, List<Boolean> unregisteredList) {
        StudentRegisterWrapper wrapper = new StudentRegisterWrapper();
        wrapper.setCourse(course);
        for (Student student : students) {
            studentIdsList.add(student.getId());
            registeredList.add(Boolean.FALSE);
        }
        for (Student student : registeredStudents) {
            registeredStudentIdsList.add(student.getId());
            unregisteredList.add(Boolean.TRUE);
        }
        wrapper.setRegisteredList(registeredList);
        wrapper.setStudentIdList(studentIdsList);
        wrapper.setUnregisteredList(unregisteredList);
        wrapper.setRegisteredStudentIdList(registeredStudentIdsList);

        return wrapper;
    }

    @PostMapping("/RegisterNew")
    public String registerStudents(@ModelAttribute("wrapper") StudentRegisterWrapper wrapper,
                                   @RequestParam(name = "courseId") Long courseId) {
        int index = 0;
        if (wrapper.getStudentIdList() != null) {
            for (Long studentId : wrapper.getStudentIdList()) {
                Boolean shouldBeAdded;
                try {
                    shouldBeAdded = wrapper.getRegisteredList().get(index);
                } catch (IndexOutOfBoundsException ex) {
                    shouldBeAdded = false;
                } catch (NullPointerException ex) {
                    shouldBeAdded = false;
                }
                if (shouldBeAdded != null && shouldBeAdded.booleanValue()) {
                    Student student = studentRepository.getOne(studentId);
                    Course course = courseRepository.getOne(courseId);
                    CourseStudentMapping mapping = new CourseStudentMapping();
                    mapping.setCourse(course);
                    mapping.setStudent(student);
                    registerStudent(mapping);
                }
                index++;
            }
        }

        index = 0;
        if (wrapper.getRegisteredStudentIdList() != null) {
            for (Long unregisteredStudent : wrapper.getRegisteredStudentIdList()) {
                Course course = courseRepository.getOne(courseId);
                Student student = studentRepository.getOne(unregisteredStudent);
                Boolean shouldBeUnregistered;
                try {
                    shouldBeUnregistered = wrapper.getUnregisteredList().get(index);
                } catch (IndexOutOfBoundsException ex) {
                    shouldBeUnregistered = false;
                } catch (NullPointerException ex) {
                    shouldBeUnregistered = false;
                }
                if (course != null && student != null && shouldBeUnregistered.booleanValue()) {
                    course.getStudents().remove(student);
                    courseRepository.save(course);
                }
                index++;
            }
        }
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

    @GetMapping("/RegisterTeacher")
    public String registerTeachertToCourse(Model model) {
        User user = userService.getLoggedUser();
        model.addAttribute("courses", courseRepository.findAll());
        model.addAttribute("teachers", teacherRepository.findAll());
        model.addAttribute("mapping", new CourseTeacherMapping());
        model.addAttribute("user", user);

        return "courseTeacherRegister_form";
    }

    @PostMapping("/Register")
    public String registerStudentToCourse(@Valid CourseStudentMapping mapping, Model model) {
        User user = userService.getLoggedUser();
        registerStudent(mapping);
        model.addAttribute("user", user);

        return INDEX_REDIRECT_STRING;
    }

    private void registerStudent(@Valid CourseStudentMapping mapping) {
        Course course = courseRepository.getOne(mapping.getCourse().getId());
        Student student = studentRepository.getOne(mapping.getStudent().getId());
        Set<Student> registeredStudents = course.getStudents();

        if (registeredStudents == null || registeredStudents.isEmpty()) {
            registeredStudents = new HashSet<>();
        }

        if (student != null) {
            registeredStudents.add(student);
        }

        if (course != null) {
            course.setStudents(registeredStudents);
            courseRepository.save(course);
        }
    }

    @PostMapping("/RegisterTeacher")
    public String registerTeacherToCourse(@Valid CourseTeacherMapping mapping, Model model) {
        User user = userService.getLoggedUser();
        registerTeacher(mapping);
        model.addAttribute("user", user);

        return INDEX_REDIRECT_STRING;
    }

    private void registerTeacher(@Valid CourseTeacherMapping mapping) {
        Course course = courseRepository.getOne(mapping.getCourse().getId());
        Teacher teacher = teacherRepository.getOne(mapping.getTeacher().getId());
        Set<Teacher> registeredTeachers = course.getTeachers();

        if (registeredTeachers == null || registeredTeachers.isEmpty()) {
            registeredTeachers = new HashSet<>();
        }

        if (teacher != null) {
            registeredTeachers.add(teacher);
        }

        if (course != null) {
            course.setTeachers(registeredTeachers);
            courseRepository.save(course);
        }
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

    @GetMapping("/RemoveTeacherFromCourse")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public String removerTeacherFromCourse(Long courseId, Long teacherId, Model model) {
        User user = userService.getLoggedUser();
        Course course = courseRepository.getOne(courseId);
        Teacher teacher = teacherRepository.getOne(teacherId);

        if (course != null && teacher != null) {
            course.getTeachers().remove(teacher);
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
