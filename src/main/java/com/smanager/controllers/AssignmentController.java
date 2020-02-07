package com.smanager.controllers;


import com.smanager.dao.models.*;
import com.smanager.dao.repositories.*;
import com.smanager.interfaces.IUserService;
import com.smanager.services.FileUploadHelper;
import com.smanager.services.RedirectService;
import com.smanager.storage.StorageService;
import com.smanager.utils.MultilineTextParser;
import com.smanager.utils.PaginationHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import javax.validation.Valid;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/Assignment")
public class AssignmentController {

    public final static String DETAILS_URL = "/Assignment/Details";
    public final static String INDEX_REDIRECT_STRING = "redirect:/Assignment/Index";

    private AssignmentRepository assignmentRepository;
    private SolutionRepository solutionRepository;
    private FileHistoryRepository fileHistoryRepository;
    private StorageService storageService;
    private TeacherRepository teacherRepository;
    private CourseRepository courseRepository;
    private FileUploadHelper fileUploadHelper;
    private IUserService userService;
    private PaginationHelper<Assignment> paginationHelper;
    private RedirectService redirectService;
    private String entryId;

    private String searchValue;

    @Autowired
    public AssignmentController(AssignmentRepository assignmentRepository, FileHistoryRepository fileHistoryRepository,
                                StorageService storageService, TeacherRepository teacherRepository,
                                CourseRepository courseRepository, IUserService userService,
                                SolutionRepository solutionRepository, RedirectService redirectService) {
        this.assignmentRepository = assignmentRepository;
        this.solutionRepository = solutionRepository;
        this.fileHistoryRepository = fileHistoryRepository;
        this.storageService = storageService;
        this.teacherRepository = teacherRepository;
        this.courseRepository = courseRepository;
        fileUploadHelper = new FileUploadHelper(fileHistoryRepository, storageService);
        this.userService = userService;
        paginationHelper = new PaginationHelper<>(assignmentRepository);
        this.redirectService = redirectService;
    }

    @GetMapping("Index")
    public String index(Model model) {
        List<Assignment> assignments;
        if (searchValue != null && !searchValue.isEmpty()) {
             assignments = assignmentRepository.findAssignmentByTitleContaining(searchValue);
        } else {
            assignments = assignmentRepository.findAll();
        }
        fillModel(model, assignments);

        this.searchValue = "";
        return "assignment_index";
    }

    @PostMapping("Search")
    public String search(@RequestParam("search") String searchValue) {
        this.searchValue = searchValue.toLowerCase().trim();
        return INDEX_REDIRECT_STRING;
    }

    @GetMapping("Index/{page}")
    public String index(Model model, @PathVariable("page") int page) {
        Page<Assignment> assignments = paginationHelper.getPage(page);
        fillModel(model, assignments);

        return "assignment_index";
    }

    private void fillModel(Model model, Iterable<Assignment> assignments) {
        User user = userService.getLoggedUser();
        updateLinks(assignments, storageService.loadAllByType(Assignment.class).collect(Collectors.toList()));
        model.addAttribute("assignments", assignments);
        model.addAttribute("files", getAllFiles());
        model.addAttribute("user", user);
        model.addAttribute("pages", paginationHelper.getPageList());
    }

    //TODO Extract getAllFiles and updateLinks to helper class
    private List<String> getAllFiles() {
        List<String> filePaths = new ArrayList<>();
        List<Path> paths = storageService.loadAllByType(Assignment.class).collect(Collectors.toList());
        for (Path path : paths) {
            String filePath = MvcUriComponentsBuilder.fromMethodName(FileUploadController.class, "serveFile", path.getFileName().toString()).build().toString();
            filePaths.add(filePath);
        }
        return filePaths;
    }

    private void updateLinks(Iterable<Assignment> assignments, List<Path> paths) {
        for (Assignment assignment : assignments) {
            for (Path path : paths) {
                if (assignment.getPath() != null && path.getFileName().toString().contains(assignment.getPath())) {
                    String filePath = MvcUriComponentsBuilder.fromMethodName(FileUploadController.class, "serveFile", path.getFileName().toString()).build().toString();
                    assignment.setPath(filePath);
                }
            }
        }
    }

    @GetMapping("Create")
    public String showForm(Model model, @RequestParam(name = "providedCourseId", required = false) Long providedCourseId) {
        User user = userService.getLoggedUser();
        fillModel(model, new Assignment(), true);
        model.addAttribute("user", user);
        Long teacherId = userService.getStudentOrTeacherId(user);
        Teacher teacher = teacherRepository.getOne(teacherId);
        model.addAttribute("teacher", teacher);

        model.addAttribute("providedCourseId", providedCourseId);
        if (providedCourseId != null) {
            Course providedCourse = courseRepository.getOne(providedCourseId);
            model.addAttribute("providedCourse", providedCourse);
        }

        return "assignment_form";
    }

    @PostMapping("Create")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public String create(@Valid Assignment assignment, @RequestParam MultipartFile file,
                         BindingResult bindingResult, Model model,
                         @RequestParam(name = "providedCourseId", required = false) Long providedCourseId) {
        User user = userService.getLoggedUser();
        if (bindingResult.hasErrors()) {
            return "assignment_form";
        }
        Long teacherId = userService.getStudentOrTeacherId(user);
        Teacher teacher = teacherRepository.getOne(teacherId);
        if (providedCourseId != null) {
            Course providedCourse = courseRepository.getOne(providedCourseId);
            assignment.setCourse(providedCourse);
        }
        assignment.setTeacher(teacher);
        assignment.setPath("");
        fileUploadHelper.saveFileToRepository(assignmentRepository, file, assignment);

        model.addAttribute("user", user);

        return redirectService.getAssignmentRedirectWorkUrl(user);
    }

    @GetMapping("/Details")
    public String details(Model model, Long id) {
        User user = userService.getLoggedUser();
        Assignment assignment = assignmentRepository.getOne(id);
        if (assignment == null) {
            return INDEX_REDIRECT_STRING;
        }
        fillModel(model, assignment);
        model.addAttribute("user", user);
        model.addAttribute("parsedContent", MultilineTextParser.getMultilineTextFromString(assignment.getContent()));

        if (assignment.getPath() != null) {
            Path path = Paths.get(assignment.getPath());
            String filePath = MvcUriComponentsBuilder.fromMethodName(FileUploadController.class,
                    "serveFile", path.getFileName().toString()).build().toString();
            model.addAttribute("uploadedFile", filePath);
        }

        return "assignment_details";
    }

    @GetMapping("/Edit")
    public String edit(Model model, Long id, @RequestParam(name = "entryId", required = false) String entryId) {
        User user = userService.getLoggedUser();
        Assignment assignment = assignmentRepository.getOne(id);
        if (assignment == null) {
            return "redirect:/Assignment/Edit?id=" + id;
        }
        fillModel(model, assignment, false);
        model.addAttribute("id", id);
        model.addAttribute("user", user);

        if (assignment.getPath() != null) {
            Path path = Paths.get(assignment.getPath());
            String filePath = MvcUriComponentsBuilder.fromMethodName(FileUploadController.class,
                    "serveFile", path.getFileName().toString()).build().toString();
            model.addAttribute("uploadedFile", filePath);
        }

        if (entryId != null) {
            this.entryId = entryId;
        }

        return "assignment_form";
    }

    @PostMapping("/Edit")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public String edit(@Valid Assignment assignment, @RequestParam MultipartFile file, BindingResult binding,
                       Model model, @RequestParam(name = "removeFile", required = false) boolean removeFile) {
        User user = userService.getLoggedUser();
        if (binding.hasErrors()) {
            return INDEX_REDIRECT_STRING;
        }

        if (assignment != null) {
            Assignment assignmentFromDb = assignmentRepository.getOne(assignment.getId());
            assignmentFromDb.setTitle(assignment.getTitle());
            assignmentFromDb.setContent(assignment.getContent());
            assignmentFromDb.setCourse(assignment.getCourse());
            assignmentFromDb.setTeacher(assignment.getTeacher());

            if (removeFile) {
                storageService.delete(assignmentFromDb.getPath());
                assignmentFromDb.setPath(null);
                if (!file.isEmpty())
                    fileUploadHelper.updateFileHistory(assignmentFromDb, file);
            } else if (!removeFile && !file.isEmpty() && assignmentFromDb.getPath() == null) {
                fileUploadHelper.updateFileHistory(assignmentFromDb, file);
            }

            assignmentRepository.save(assignmentFromDb);
        }
        model.addAttribute("user", user);

        return redirectService.getAssignmentRedirectWorkUrlAndScrollToSelectedId(user, this.entryId);
    }

    @GetMapping("/Delete")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public String delete(Long id, Model model) {
        User user = userService.getLoggedUser();
        Assignment assignment = assignmentRepository.getOne(id);
        if (assignment != null) {
            for (Solution solution : assignment.getSolutions()) {
                solutionRepository.delete(solution);
            }
            assignmentRepository.delete(assignment);
        }
        model.addAttribute("user", user);

        return INDEX_REDIRECT_STRING;
    }

    private void fillModel(Model model, Assignment assignment, boolean isCreateAction) {
        fillModel(model, assignment);
        model.addAttribute("isCreateAction", isCreateAction);
    }

    private void fillModel(Model model, Assignment assignment) {
        model.addAttribute("assignment", assignment);
        model.addAttribute("teachers", teacherRepository.findAll());
        model.addAttribute("courses", courseRepository.findAll());
    }

    @GetMapping("/CourseAssignments")
    public String getAssignmentsForCourse(Model model, Long courseId) {
        User user = userService.getLoggedUser();
        Course course = courseRepository.getOne(courseId);
        if (course == null) {
            return INDEX_REDIRECT_STRING;
        }
        List<Assignment> assignments = assignmentRepository.findAllByCourseId(courseId);
        model.addAttribute("course", course);
        model.addAttribute("assignments", assignments);
        model.addAttribute("user", user);

        return "courseAssignments_index";
    }
}
