package com.smanager.controllers;

import com.smanager.Cache;
import com.smanager.dao.models.*;
import com.smanager.dao.repositories.*;
import com.smanager.interfaces.IUserService;
import com.smanager.storage.StorageService;
import com.smanager.utils.CourseHelper;
import com.smanager.utils.GradesUtil;
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
    private GradesUtil gradesUtil;

    @Autowired
    public WorkController(CourseRepository courseRepository, AssignmentRepository assignmentRepository,
                          StudentRepository studentRepository, UserRepository userRepository,
                          TeacherRepository teacherRepository, IUserService userService, StorageService storageService,
                          GradesUtil gradesUtil) {
        this.studentRepository = studentRepository;
        this.userRepository = userRepository;
        this.teacherRepository = teacherRepository;
        this.courseHelper = new CourseHelper(courseRepository, assignmentRepository);
        this.userService = userService;
        this.storageService = storageService;
        userCoursesCache = new HashMap<>();
        this.gradesUtil = gradesUtil;
    }

    @GetMapping(WorkControllerPaths.INDEX)
    @PreAuthorize("hasRole('STUDENT')")
    public String index(Model model, @RequestParam(value = "courseToDisplay", required = false) Long courseToDisplay) {
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
        CourseAssignmentSolutionWrapper wrapper = getCourses(courseToDisplay, studentId, s -> s.getStudent().getId().equals(studentId));
        CourseAssignmentSolutionWrapper cachedCourses = getCourses(null, studentId, s -> s.getStudent().getId().equals(studentId));
        List<String> paths = getPaths();
        fillModelForStudentReports(model, student, wrapper, user, paths, cachedCourses);
        Cache.put(user.getId(), WorkControllerPaths.getRedirectPath(WorkControllerPaths.INDEX));
        model.addAttribute("url", WorkControllerPaths.getUrl(WorkControllerPaths.INDEX));
        model.addAttribute("divId", Cache.getDiv(user.getId()));

        return "work_index";
    }

    @PostMapping(WorkControllerPaths.INDEX)
    public String studentWork(Long courseToDisplay) {
        return WorkControllerPaths.getRedirectPath(WorkControllerPaths.INDEX) + "?shouldUpdateCache=true&courseToDisplay=" + courseToDisplay;
    }

    @GetMapping(WorkControllerPaths.MENU)
    public String sideMenu(Model model) {
        User user = userService.getLoggedUser();
        Long studentOrTeacherId = userService.getStudentOrTeacherId(user);
        CourseAssignmentSolutionWrapper wrapper = courseHelper.populateCoursesAssignmentsAndSolutions(studentOrTeacherId, s -> s.getId() > 0, UserType.ADMIN);
        CourseAssignmentSolutionWrapper cachedCourses = courseHelper.populateCoursesAssignmentsAndSolutions(studentOrTeacherId, s -> s.getId() > 0, UserType.ADMIN);
        Teacher teacher = null;
        if (user != null) {
            if (user.getTeacherUser() != null) {
                teacher = teacherRepository.getOne(user.getTeacherUser().getId());
            } else {
                return "redirect:/Index";
            }
        }
        List<String> paths = getPaths();
        fillModelForTeacherReports(model, teacher, wrapper, user, paths, cachedCourses);
        Cache.put(user.getId(), WorkControllerPaths.getRedirectPath(WorkControllerPaths.MENU));

        return "side_menu";
    }

    @GetMapping(WorkControllerPaths.TEACHER_WORK)
    @PreAuthorize("hasRole('TEACHER')")
    public String teacherWork(Model model, @RequestParam(value = "shouldUpdateCache", required = false) boolean shouldUpdateCache,
                              @RequestParam(value = "courseToDisplay", required = false) Long courseToDisplay) {
        ReportWrapper methodReportWrapper = getData();
        User user = userService.getLoggedUser();
        Long teacherId = methodReportWrapper.getTeacher().getId();
        CourseAssignmentSolutionWrapper wrapper = getCourses(courseToDisplay, teacherId, s -> s.isFinished());
        CourseAssignmentSolutionWrapper cachedCourses = getCourses(null, teacherId, s -> s.isFinished());
        List<String> paths = getPaths();
        fillModelForTeacherReports(model, methodReportWrapper.getTeacher(), wrapper, methodReportWrapper.getUser(), paths, cachedCourses);
        Cache.put(userService.getLoggedUser().getId(), WorkControllerPaths.getRedirectPath(WorkControllerPaths.TEACHER_WORK));
        updateCache(teacherId);
        model.addAttribute("url", WorkControllerPaths.getUrl(WorkControllerPaths.TEACHER_WORK));
        model.addAttribute("divId", Cache.getDiv(user.getId()));

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

        CourseAssignmentSolutionWrapper wrapper = getCourses(courseToDisplay, teacherId, s -> s.getGrade() == Grades.NO_GRADE.getGrade());
        CourseAssignmentSolutionWrapper cachedCourses = getCourses(null, teacherId, s -> s.getGrade() == Grades.NO_GRADE.getGrade());
        List<String> paths = getPaths();
        fillModelForTeacherReports(model, methodReportWrapper.getTeacher(), wrapper, methodReportWrapper.getUser(), paths, cachedCourses);
        Cache.put(userService.getLoggedUser().getId(), WorkControllerPaths.getRedirectPath(WorkControllerPaths.NO_GRADES_REPORT));
        updateCache(teacherId);
        model.addAttribute("url", WorkControllerPaths.getUrl(WorkControllerPaths.NO_GRADES_REPORT));

        return methodReportWrapper.isErrorOccurred() ? WorkControllerPaths.getRedirectPath(WorkControllerPaths.TEACHER_WORK)  : "work_index_teacher";
    }

    @GetMapping(WorkControllerPaths.NO_COMMENT_REPORT)
    @PreAuthorize("hasRole('TEACHER')")
    public String generateReportForTeacherWithSolutionsWithoutComment(Model model, @RequestParam(value = "shouldUpdateCache", required = false) boolean shouldUpdateCache,
                                @RequestParam(value = "courseToDisplay", required = false) Long courseToDisplay) {
        ReportWrapper methodReportWrapper = getData();
        Long teacherId = methodReportWrapper.getTeacher().getId();
        CourseAssignmentSolutionWrapper wrapper = getCourses(courseToDisplay, teacherId, s -> s.getComments().isEmpty());
        CourseAssignmentSolutionWrapper cachedCourses = getCourses(null, teacherId, s -> s.getComments().isEmpty());
        List<String> paths = getPaths();
        fillModelForTeacherReports(model, methodReportWrapper.getTeacher(), wrapper, methodReportWrapper.getUser(), paths, cachedCourses);
        Cache.put(userService.getLoggedUser().getId(), WorkControllerPaths.getRedirectPath(WorkControllerPaths.NO_COMMENT_REPORT));
        updateCache(teacherId);
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
            CustomGrade grade = gradesUtil.getCourseGrade(id, course.getId());
            if (grade == null) {
                grade = gradesUtil.getGradeIfNull(id, course.getId());
            }
            List<Double> grades = gradesUtil.getAssignmentGrades(id, course.getId());
            GradeWrapper gw = new GradeWrapper(grade != null ? grade.getGrade() : 0, grades);
            courseGradeMap.put(course, gw);
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
            CustomGrade grade = gradesUtil.getCourseGrade(studentId, course.getId());
            if (grade == null) {
                grade = gradesUtil.getGradeIfNull(studentId, course.getId());
            }
            List<Double> grades = gradesUtil.getAssignmentGrades(studentId, course.getId());
            GradeWrapper gw = new GradeWrapper(grade != null ? grade.getGrade() : 0, grades);
            courseGradeMap.put(course, gw);
        }

        model.addAttribute("courseGrades", courseGradeMap);
        model.addAttribute("user", user);
        model.addAttribute("viewedUser", viewedUser);
        Cache.put(userService.getLoggedUser().getId(), WorkControllerPaths.getRedirectPath(WorkControllerPaths.TEACHER_GRADES_REPORT));

        return "teacher_grade_report";
    }

    private void fillModelForTeacherReports(Model model, Teacher teacher, CourseAssignmentSolutionWrapper wrapper,
                                     User user, List<String> paths, CourseAssignmentSolutionWrapper cachedCourses) {
        fillModelForReports(model, wrapper, user, paths, cachedCourses);
        model.addAttribute("teacher", teacher);
        model.addAttribute("reports", teacherReportList);
    }


    private void fillModelForStudentReports(Model model, Student student, CourseAssignmentSolutionWrapper wrapper,
                                            User user, List<String> paths, CourseAssignmentSolutionWrapper cachedCourses) {
        fillModelForReports(model, wrapper, user, paths, cachedCourses);
        model.addAttribute("student", student);
        model.addAttribute("reports", studentReportList);
    }

    private void fillModelForReports(Model model, CourseAssignmentSolutionWrapper wrapper, User user,
                                     List<String> paths, CourseAssignmentSolutionWrapper cachedCourses) {
        model.addAttribute("courses", wrapper.getCourses());
        model.addAttribute("cachedCourses", cachedCourses.getCourses());
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
