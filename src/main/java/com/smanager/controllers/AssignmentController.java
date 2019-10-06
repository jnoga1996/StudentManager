package com.smanager.controllers;


import com.smanager.dao.models.*;
import com.smanager.dao.repositories.*;
import com.smanager.services.FileUploadHelper;
import com.smanager.services.UserService;
import com.smanager.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/Assignment")
public class AssignmentController {

    private final static String INDEX_REDIRECT_STRING = "redirect:/Assignment/Index";

    private AssignmentRepository assignmentRepository;
    private FileHistoryRepository fileHistoryRepository;
    private StorageService storageService;
    private TeacherRepository teacherRepository;
    private CourseRepository courseRepository;
    private FileUploadHelper fileUploadHelper;
    private UserRepository userRepository;
    private UserService userService;

    @Autowired
    public AssignmentController(AssignmentRepository assignmentRepository, FileHistoryRepository fileHistoryRepository,
                                StorageService storageService, TeacherRepository teacherRepository,
                                CourseRepository courseRepository, UserRepository userRepository) {
        this.assignmentRepository = assignmentRepository;
        this.fileHistoryRepository = fileHistoryRepository;
        this.storageService = storageService;
        this.teacherRepository = teacherRepository;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        fileUploadHelper = new FileUploadHelper(fileHistoryRepository, storageService);
        userService = new UserService(SecurityContextHolder.getContext().getAuthentication(), userRepository);
    }

    @GetMapping("Index")
    public String index(Model model) {
        model.addAttribute("assignments", assignmentRepository.findAll());
        model.addAttribute("files", storageService.loadAllByType(Assignment.class).map(
                path -> MvcUriComponentsBuilder.fromMethodName(FileUploadController.class,
                        "serveFile", path.getFileName().toString()).build().toString())
                .collect(Collectors.toList()));
        model.addAttribute("user", userService.getLoggedUser());

        return "assignment_index";
    }

    @GetMapping("Create")
    public String showForm(Model model) {
        fillModel(model, new Assignment(), true);
        model.addAttribute("user", userService.getLoggedUser());

        return "assignment_form";
    }

    @PostMapping("Create")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public String create(@Valid Assignment assignment, @RequestParam MultipartFile file, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "assignment_form";
        }

        fileUploadHelper.saveFileToRepository(assignmentRepository, file, assignment);
        model.addAttribute("user", userService.getLoggedUser());

        return INDEX_REDIRECT_STRING;
    }

    @GetMapping("/Edit")
    public String edit(Model model, Long id) {
        Assignment assignment = assignmentRepository.getOne(id);
        if (assignment == null) {
            return "redirect:/Assignment/Edit?id=" + id;
        }
        fillModel(model, assignment, false);
        model.addAttribute("id", id);
        model.addAttribute("user", userService.getLoggedUser());

        return "assignment_form";
    }

    @PostMapping("/Edit")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public String edit(@Valid Assignment assignment, @RequestParam MultipartFile file, BindingResult binding, Model model) {
        if (binding.hasErrors()) {
            return INDEX_REDIRECT_STRING;
        }

        if (assignment != null) {
            Assignment assignmentFromDb = assignmentRepository.getOne(assignment.getId());
            assignmentFromDb.setTitle(assignment.getTitle());
            assignmentFromDb.setContent(assignment.getContent());
            assignmentFromDb.setCourse(assignment.getCourse());
            assignmentFromDb.setTeacher(assignment.getTeacher());

            fileUploadHelper.updateFileHistory(assignmentFromDb, file);

            assignmentRepository.save(assignmentFromDb);
        }
        model.addAttribute("user", userService.getLoggedUser());

        return INDEX_REDIRECT_STRING;
    }

    @GetMapping("/Delete")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public String delete(Long id, Model model) {
        Assignment assignment = assignmentRepository.getOne(id);
        if (assignment != null) {
            assignmentRepository.delete(assignment);
        }
        model.addAttribute("user", userService.getLoggedUser());

        return INDEX_REDIRECT_STRING;
    }

    private void fillModel(Model model, Assignment assignment, boolean isCreateAction) {
        model.addAttribute("assignment", assignment);
        model.addAttribute("isCreateAction", isCreateAction);
        model.addAttribute("teachers", teacherRepository.findAll());
        model.addAttribute("courses", courseRepository.findAll());
    }

    @GetMapping("/CourseAssignments")
    public String getAssignmentsForCourse(Model model, Long courseId) {
        Course course = courseRepository.getOne(courseId);
        if (course == null) {
            return INDEX_REDIRECT_STRING;
        }
        List<Assignment> assignments = assignmentRepository.findAllByCourseId(courseId);
        model.addAttribute("course", course);
        model.addAttribute("assignments", assignments);
        model.addAttribute("user", userService.getLoggedUser());

        return "courseAssignments_index";
    }
}
