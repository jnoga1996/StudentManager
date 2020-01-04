package com.smanager.controllers;

import com.smanager.Cache;
import com.smanager.dao.models.*;
import com.smanager.dao.repositories.*;
import com.smanager.services.UserService;
import com.smanager.storage.StorageService;
import com.smanager.utils.CourseHelper;
import com.smanager.wrappers.CourseAssignmentSolutionWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/Work")
public class WorkController {

    private StudentRepository studentRepository;
    private TeacherRepository teacherRepository;
    private UserRepository userRepository;
    private UserService userService;
    private List<String> teacherReportList = Arrays.asList(
            "/TeacherWork",
            "/NoGradeReport",
            "/NoCommentReport"
            );
    private List<String> studentReportList = Arrays.asList(
            "/GradesReport"
    );
    private CourseHelper courseHelper;
    private StorageService storageService;

    @Autowired
    public WorkController(CourseRepository courseRepository, AssignmentRepository assignmentRepository,
                          StudentRepository studentRepository, UserRepository userRepository,
                          TeacherRepository teacherRepository, UserService userService, StorageService storageService) {
        this.studentRepository = studentRepository;
        this.userRepository = userRepository;
        this.teacherRepository = teacherRepository;
        this.courseHelper = new CourseHelper(courseRepository, assignmentRepository);
        this.userService = userService;
        this.storageService = storageService;
    }

    @GetMapping("/Index")
    @PreAuthorize("hasRole('STUDENT')")
    public String index(Model model) {
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
        CourseAssignmentSolutionWrapper wrapper = courseHelper.populateCoursesAssignmentsAndSolutions(studentId, s -> s.getStudent().getId().equals(studentId), UserType.STUDENT);
        List<String> paths = getPaths();
        fillModelForStudentReports(model, student, wrapper, user, paths);


        return "work_index";
    }

    @GetMapping("/Menu")
    public String sideMenu(Model model) {
        User user = userService.getLoggedUser();
        Long studentOrTeacherId = userService.getStudentOrTeacherId(user);
        CourseAssignmentSolutionWrapper wrapper = courseHelper.populateCoursesAssignmentsAndSolutions(studentOrTeacherId, s -> s.getId() > 0, UserType.ADMIN);
        Teacher teacher = null;
        if (user != null) {
            if (user.getTeacherUser() != null) {
                teacher = teacherRepository.getOne(user.getTeacherUser().getId());
            } else {
                return "redirect:/Index";
            }
        }
        List<String> paths = getPaths();
        fillModelForTeacherReports(model, teacher, wrapper, user, paths);

        return "side_menu";
    }

    @GetMapping("/TeacherWork")
    @PreAuthorize("hasRole('TEACHER')")
    public String teacherWork(Model model) {
        ReportWrapper methodReportWrapper = getData();
        Long teacherId = methodReportWrapper.getTeacher().getId();
        CourseAssignmentSolutionWrapper wrapper = courseHelper.populateCoursesAssignmentsAndSolutions(teacherId, s -> s.isFinished(), UserType.TEACHER);
        List<String> paths = getPaths();
        fillModelForTeacherReports(model, methodReportWrapper.getTeacher(), wrapper, methodReportWrapper.getUser(), paths);
        Cache.put(userService.getLoggedUser().getId(), "redirect:/Work/TeacherWork");

        return methodReportWrapper.isErrorOccurred() ? "redirect:/Work/Index" : "work_index_teacher";
    }

    @GetMapping("/NoGradeReport")
    @PreAuthorize("hasRole('TEACHER')")
    public String generateReportForTeacherWithSolutionsWithoutGrade(Model model) {
        ReportWrapper methodReportWrapper = getData();
        Long teacherId = methodReportWrapper.getTeacher().getId();

        CourseAssignmentSolutionWrapper wrapper = courseHelper.populateCoursesAssignmentsAndSolutions(teacherId, s -> s.getGrade() == Grades.NO_GRADE.getGrade(), UserType.TEACHER);
        List<String> paths = getPaths();
        fillModelForTeacherReports(model, methodReportWrapper.getTeacher(), wrapper, methodReportWrapper.getUser(), paths);
        Cache.put(userService.getLoggedUser().getId(), "redirect:/Work/NoGradeReport");

        return methodReportWrapper.isErrorOccurred() ? "redirect:/Index" : "work_index_teacher";
    }

    @GetMapping("/NoCommentReport")
    @PreAuthorize("hasRole('TEACHER')")
    public String generateReportForTeacherWithSolutionsWithoutComment(Model model) {
        ReportWrapper methodReportWrapper = getData();
        Long teacherId = methodReportWrapper.getTeacher().getId();
        CourseAssignmentSolutionWrapper wrapper = courseHelper.populateCoursesAssignmentsAndSolutions(teacherId, s -> s.getComments().isEmpty(), UserType.TEACHER);
        List<String> paths = getPaths();
        fillModelForTeacherReports(model, methodReportWrapper.getTeacher(), wrapper, methodReportWrapper.getUser(), paths);
        Cache.put(userService.getLoggedUser().getId(), "redirect:/Work/NoCommentReport");

        return methodReportWrapper.isErrorOccurred() ? "redirect:/Index" : "work_index_teacher";
    }

    @GetMapping("/GradesReport")
    @PreAuthorize("hasRole('STUDENT')")
    public String generateGradesReportForStudent(Model model) {
        User user = userService.getLoggedUser();
        Long id = userService.getStudentOrTeacherId(user);

        List<Course> courses = courseHelper.getCourseRepository().findCoursesByStudentId(id);
        Map<Course, GradeWrapper> courseGradeMap = new HashMap<>();

        for (Course course : courses) {
            int grade = CourseHelper.getGradeForCourse(course, id);
            List<Integer> courseGrades = CourseHelper.getGradesForCourse(course, id);
            courseGradeMap.put(course, new GradeWrapper(grade, courseGrades));
        }

        model.addAttribute("courseGrades", courseGradeMap);
        model.addAttribute("user", user);

        return "grade_report";
    }

    private class GradeWrapper {
        private Integer grade;
        private List<Integer> grades;

        public GradeWrapper(Integer grade, List<Integer> grades) {
            this.grade = grade;
            this.grades = grades;
        }

        public Integer getGrade() {
            return grade;
        }

        public void setGrade(Integer grade) {
            this.grade = grade;
        }

        public List<Integer> getGrades() {
            return grades;
        }

        public void setGrades(List<Integer> grades) {
            this.grades = grades;
        }
    }

    private void fillModelForTeacherReports(Model model, Teacher teacher, CourseAssignmentSolutionWrapper wrapper,
                                     User user, List<String> paths) {
        fillModelForReports(model, wrapper, user, paths);
        model.addAttribute("teacher", teacher);
        model.addAttribute("reports", teacherReportList);
    }

    private void fillModelForStudentReports(Model model, Student student, CourseAssignmentSolutionWrapper wrapper,
                                            User user, List<String> paths) {
        fillModelForReports(model, wrapper, user, paths);
        model.addAttribute("student", student);
        model.addAttribute("reports", studentReportList);
    }

    private void fillModelForReports(Model model, CourseAssignmentSolutionWrapper wrapper, User user, List<String> paths) {
        model.addAttribute("courses", wrapper.getCourses());
        model.addAttribute("courseAssignments", wrapper.getCourseAssignmentsMap());
        model.addAttribute("assignmentsSolutions", wrapper.getAssignmentSolutionsMap());
        model.addAttribute("user", user);
        model.addAttribute("paths", paths);
    }

    private class ReportWrapper {
        private boolean errorOccurred;
        private User user;
        private Teacher teacher;

        public boolean isErrorOccurred() {
            return errorOccurred;
        }

        public void setErrorOccurred(boolean errorOccurred) {
            this.errorOccurred = errorOccurred;
        }

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }

        public Teacher getTeacher() {
            return teacher;
        }

        public void setTeacher(Teacher teacher) {
            this.teacher = teacher;
        }

        public ReportWrapper(boolean result, User user, Teacher teacher) {
            this.errorOccurred = result;
            this.user = user;
            this.teacher = teacher;
        }
    }

    public ReportWrapper getData() {
        boolean errorOccurred = false;
        User user = userService.getLoggedUser();
        Teacher teacher = null;
        if (user != null) {
            if (user.getTeacherUser() != null) {
                teacher = teacherRepository.getOne(user.getTeacherUser().getId());
            } else {
                errorOccurred = true;
            }
        }

        return new ReportWrapper(errorOccurred, user, teacher);
    }

    private List<String> getPaths() {
        return storageService.loadAllByType(Solution.class).map(
                path -> MvcUriComponentsBuilder.fromMethodName(FileUploadController.class,
                        "serveFile", path.getFileName().toString()).build().toString())
                .collect(Collectors.toList());
    }

}
