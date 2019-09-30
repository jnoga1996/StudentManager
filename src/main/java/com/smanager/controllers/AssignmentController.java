package com.smanager.controllers;


import com.smanager.dao.models.Assignment;
import com.smanager.dao.models.Course;
import com.smanager.dao.models.FileHistory;
import com.smanager.dao.models.FileType;
import com.smanager.dao.repositories.AssignmentRepository;
import com.smanager.dao.repositories.CourseRepository;
import com.smanager.dao.repositories.FileHistoryRepository;
import com.smanager.dao.repositories.TeacherRepository;
import com.smanager.services.FileUploadHelper;
import com.smanager.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @Autowired
    public AssignmentController(AssignmentRepository assignmentRepository, FileHistoryRepository fileHistoryRepository,
                                StorageService storageService, TeacherRepository teacherRepository,
                                CourseRepository courseRepository) {
        this.assignmentRepository = assignmentRepository;
        this.fileHistoryRepository = fileHistoryRepository;
        this.storageService = storageService;
        this.teacherRepository = teacherRepository;
        this.courseRepository = courseRepository;
        fileUploadHelper = new FileUploadHelper(fileHistoryRepository, storageService);
    }

    @GetMapping("Index")
    public String index(Model model) {
        model.addAttribute("assignments", assignmentRepository.findAll());
        model.addAttribute("files", storageService.loadAllByType(Assignment.class).map(
                path -> MvcUriComponentsBuilder.fromMethodName(FileUploadController.class,
                        "serveFile", path.getFileName().toString()).build().toString())
                .collect(Collectors.toList()));
        return "assignment_index";
    }

    @GetMapping("Create")
    public String showForm(Model model) {
        fillModel(model, new Assignment(), true);
        return "assignment_form";
    }

    @PostMapping("Create")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public String create(@Valid Assignment assignment, @RequestParam MultipartFile file, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "assignment_form";
        }

        fileUploadHelper.saveFileToRepository(assignmentRepository, file, assignment);

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

        return INDEX_REDIRECT_STRING;
    }

    @GetMapping("/Delete")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public String delete(Long id) {
        Assignment assignment = assignmentRepository.getOne(id);
        if (assignment != null) {
            assignmentRepository.delete(assignment);
        }

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

        return "courseAssignments_index";
    }
}
