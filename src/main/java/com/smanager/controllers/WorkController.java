package com.smanager.controllers;

import com.smanager.Cache;
import com.smanager.dao.models.*;
import com.smanager.dao.repositories.*;
import com.smanager.interfaces.IUserService;
import com.smanager.storage.StorageService;
import com.smanager.utils.CourseHelper;
import com.smanager.utils.WorkControllerPaths;
import com.smanager.wrappers.CourseAssignmentSolutionWrapper;
import com.smanager.wrappers.GradeWrapper;
import com.smanager.wrappers.ReportWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/Work")
public class WorkController {

    private StudentRepository studentRepository;
    private TeacherRepository teacherRepository;
    private UserRepository userRepository;
    private IUserService userService;
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
    private Map<Long, List<Course>> userCoursesCache;

    @Autowired
    public WorkController(CourseRepository courseRepository, AssignmentRepository assignmentRepository,
                          StudentRepository studentRepository, UserRepository userRepository,
                          TeacherRepository teacherRepository, IUserService userService, StorageService storageService) {
        this.studentRepository = studentRepository;
        this.userRepository = userRepository;
        this.teacherRepository = teacherRepository;
        this.courseHelper = new CourseHelper(courseRepository, assignmentRepository);
        this.userService = userService;
        this.storageService = storageService;
        userCoursesCache = new HashMap<>();
    }

    @GetMapping(WorkControllerPaths.INDEX)
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
        Cache.put(user.getId(), WorkControllerPaths.getRedirectPath(WorkControllerPaths.INDEX));

        return "work_index";
    }

    @GetMapping(WorkControllerPaths.MENU)
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
        Cache.put(user.getId(), WorkControllerPaths.getRedirectPath(WorkControllerPaths.MENU));

        return "side_menu";
    }

    @GetMapping(WorkControllerPaths.TEACHER_WORK)
    @PreAuthorize("hasRole('TEACHER')")
    public String teacherWork(Model model, @RequestParam(value = "shouldUpdateCache", required = false) boolean shouldUpdateCache,
                              @RequestParam(value = "courseToDisplay", required = false) Long courseToDisplay) {
        ReportWrapper methodReportWrapper = getData();
        Long teacherId = methodReportWrapper.getTeacher().getId();
        CourseAssignmentSolutionWrapper wrapper = getCourses(courseToDisplay, teacherId, s -> s.isFinished());
        List<String> paths = getPaths();
        fillModelForTeacherReports(model, methodReportWrapper.getTeacher(), wrapper, methodReportWrapper.getUser(), paths);
        Cache.put(userService.getLoggedUser().getId(), WorkControllerPaths.getRedirectPath(WorkControllerPaths.TEACHER_WORK));
        updateCache(teacherId);
        model.addAttribute("cachedCourses", userCoursesCache.get(teacherId));
        model.addAttribute("url", WorkControllerPaths.getUrl(WorkControllerPaths.TEACHER_WORK));

        return methodReportWrapper.isErrorOccurred() ? WorkControllerPaths.getRedirectPath(WorkControllerPaths.TEACHER_WORK) : "work_index_teacher";
    }

    private CourseAssignmentSolutionWrapper getCourses(Long courseToDisplay, Long teacherId, Predicate<Solution> predicate) {
        if (courseToDisplay != null) {
            return courseHelper.populateCoursesAssignmentsAndSolutions(teacherId, predicate, UserType.TEACHER, courseToDisplay);
        } else {
            return courseHelper.populateCoursesAssignmentsAndSolutions(teacherId, predicate, UserType.TEACHER);
        }
    }

    private void updateCache(Long teacherId) {
        if (userCoursesCache.containsKey(teacherId)) {
            userCoursesCache.remove(teacherId);
        }
        userCoursesCache.put(teacherId, courseHelper.populateCoursesAssignmentsAndSolutions(teacherId, s -> s.isFinished(), UserType.TEACHER).getCourses());
    }

    @PostMapping(WorkControllerPaths.TEACHER_WORK)
    public String teacherWork(Long courseToDisplay) {
        return WorkControllerPaths.getRedirectPath(WorkControllerPaths.TEACHER_WORK) + "?shouldUpdateCache=true&courseToDisplay=" + courseToDisplay;
    }

    @PostMapping(WorkControllerPaths.NO_GRADES_REPORT)
    public String noGradesReport(Long courseToDisplay) {
        return WorkControllerPaths.getRedirectPath(WorkControllerPaths.NO_GRADES_REPORT) + "?shouldUpdateCache=true&courseToDisplay=" + courseToDisplay;
    }

    @PostMapping(WorkControllerPaths.NO_COMMENT_REPORT)
    public String noCommentsReport(Long courseToDisplay) {
        return WorkControllerPaths.getRedirectPath(WorkControllerPaths.NO_COMMENT_REPORT) + "?shouldUpdateCache=true&courseToDisplay=" + courseToDisplay;
    }

    @GetMapping(WorkControllerPaths.NO_GRADES_REPORT)
    @PreAuthorize("hasRole('TEACHER')")
    public String generateReportForTeacherWithSolutionsWithoutGrade(Model model, @RequestParam(value = "shouldUpdateCache", required = false) boolean shouldUpdateCache,
                                @RequestParam(value = "courseToDisplay", required = false) Long courseToDisplay) {
        ReportWrapper methodReportWrapper = getData();
        Long teacherId = methodReportWrapper.getTeacher().getId();

        CourseAssignmentSolutionWrapper wrapper = getCourses(courseToDisplay, teacherId, s -> s.getGrade() == Grades.NO_GRADE.getGrade());//courseHelper.populateCoursesAssignmentsAndSolutions(teacherId, s -> s.getGrade() == Grades.NO_GRADE.getGrade(), UserType.TEACHER);
        List<String> paths = getPaths();
        fillModelForTeacherReports(model, methodReportWrapper.getTeacher(), wrapper, methodReportWrapper.getUser(), paths);
        Cache.put(userService.getLoggedUser().getId(), WorkControllerPaths.getRedirectPath(WorkControllerPaths.NO_GRADES_REPORT));
        updateCache(teacherId);
        model.addAttribute("cachedCourses", userCoursesCache.get(teacherId));
        model.addAttribute("url", WorkControllerPaths.getUrl(WorkControllerPaths.NO_GRADES_REPORT));

        return methodReportWrapper.isErrorOccurred() ? WorkControllerPaths.getRedirectPath(WorkControllerPaths.TEACHER_WORK)  : "work_index_teacher";
    }

    @GetMapping(WorkControllerPaths.NO_COMMENT_REPORT)
    @PreAuthorize("hasRole('TEACHER')")
    public String generateReportForTeacherWithSolutionsWithoutComment(Model model, @RequestParam(value = "shouldUpdateCache", required = false) boolean shouldUpdateCache,
                                @RequestParam(value = "courseToDisplay", required = false) Long courseToDisplay) {
        ReportWrapper methodReportWrapper = getData();
        Long teacherId = methodReportWrapper.getTeacher().getId();
        CourseAssignmentSolutionWrapper wrapper = getCourses(courseToDisplay, teacherId, s -> s.getComments().isEmpty());//courseHelper.populateCoursesAssignmentsAndSolutions(teacherId, s -> s.getComments().isEmpty(), UserType.TEACHER);
        List<String> paths = getPaths();
        fillModelForTeacherReports(model, methodReportWrapper.getTeacher(), wrapper, methodReportWrapper.getUser(), paths);
        Cache.put(userService.getLoggedUser().getId(), WorkControllerPaths.getRedirectPath(WorkControllerPaths.NO_COMMENT_REPORT));
        updateCache(teacherId);
        model.addAttribute("cachedCourses", userCoursesCache.get(teacherId));
        model.addAttribute("url", WorkControllerPaths.getUrl(WorkControllerPaths.NO_COMMENT_REPORT));

        return methodReportWrapper.isErrorOccurred() ? WorkControllerPaths.getRedirectPath(WorkControllerPaths.TEACHER_WORK)  : "work_index_teacher";
    }

    @GetMapping(WorkControllerPaths.GRADES_REPORT)
    @PreAuthorize("hasRole('STUDENT')")
    public String generateGradesReportForStudent(Model model) {
        User user = userService.getLoggedUser();
        Long id = userService.getStudentOrTeacherId(user);

        List<Course> courses = courseHelper.getCourseRepository().findCoursesByStudentId(id);
        Map<Course, GradeWrapper> courseGradeMap = new HashMap<>();

        for (Course course : courses) {
            List<Assignment> studentAssignmentsForCourse = course.getAssignments()
                    .stream()
                    .filter(a -> a.getStudent().getId() == id)
                    .collect(Collectors.toList());
            Optional<Long> courseTeacherId = studentAssignmentsForCourse.stream().map(a -> a.getTeacher().getId()).findFirst();
            int grade = 0;
            if (courseTeacherId.isPresent()) {
                Long teacherId = courseTeacherId.get().longValue();
                grade = CourseHelper.getGradeForCourse(course, id, teacherId);
            }
            List<Integer> courseGrades = CourseHelper.getGradesForCourse(course, id);
            courseGradeMap.put(course, new GradeWrapper(grade, courseGrades));
        }

        model.addAttribute("courseGrades", courseGradeMap);
        model.addAttribute("user", user);
        Cache.put(userService.getLoggedUser().getId(), WorkControllerPaths.getRedirectPath(WorkControllerPaths.GRADES_REPORT));

        return "grade_report";
    }

    @GetMapping(WorkControllerPaths.TEACHER_GRADES_REPORT)
    public String generateGradesReportForTeacher(Model model, Long id) {
        User user = userRepository.getUserById(id);
        if (id == null) {
            return WorkControllerPaths.getRedirectPath(WorkControllerPaths.TEACHER_WORK);
        }
        User viewedUser = userRepository.getUserById(id);
        Long studentId = userService.getStudentOrTeacherId(user);

        List<Course> courses = courseHelper.getCourseRepository().findCoursesByStudentId(studentId);
        Map<Course, GradeWrapper> courseGradeMap = new HashMap<>();

        for (Course course : courses) {
            int grade = CourseHelper.getGradeForCourse(course, studentId, userService.getLoggedUser().getTeacherUser().getId());
            List<Integer> courseGrades = CourseHelper.getGradesForCourse(course, studentId);
            courseGradeMap.put(course, new GradeWrapper(grade, courseGrades));
        }

        model.addAttribute("courseGrades", courseGradeMap);
        model.addAttribute("user", user);
        model.addAttribute("viewedUser", viewedUser);
        Cache.put(userService.getLoggedUser().getId(), WorkControllerPaths.getRedirectPath(WorkControllerPaths.TEACHER_GRADES_REPORT));

        return "teacher_grade_report";
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
