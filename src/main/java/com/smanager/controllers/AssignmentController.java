package com.smanager.controllers;


import com.smanager.dao.models.Assignment;
import com.smanager.dao.models.FileHistory;
import com.smanager.dao.models.FileType;
import com.smanager.dao.repositories.AssignmentRepository;
import com.smanager.dao.repositories.CourseRepository;
import com.smanager.dao.repositories.FileHistoryRepository;
import com.smanager.dao.repositories.TeacherRepository;
import com.smanager.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.query.Jpa21Utils;
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
import java.nio.file.Path;
import java.util.Date;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
@RequestMapping("/Assignment")
public class AssignmentController {

    private AssignmentRepository assignmentRepository;
    private FileHistoryRepository fileHistoryRepository;
    private StorageService storageService;
    private TeacherRepository teacherRepository;
    private CourseRepository courseRepository;

    @Autowired
    public AssignmentController(AssignmentRepository assignmentRepository, FileHistoryRepository fileHistoryRepository,
                                StorageService storageService, TeacherRepository teacherRepository,
                                CourseRepository courseRepository) {
        this.assignmentRepository = assignmentRepository;
        this.fileHistoryRepository = fileHistoryRepository;
        this.storageService = storageService;
        this.teacherRepository = teacherRepository;
        this.courseRepository = courseRepository;
    }

    @GetMapping("Index")
    public String index(Model model) {
        model.addAttribute("assignments", assignmentRepository.findAll());
        Stream<Path> stream = storageService.loadAll();
        model.addAttribute("files", storageService.loadAll().map(
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
    public String create(@Valid Assignment assignment, @RequestParam MultipartFile file, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "assignment_form";
        }

        assignmentRepository.save(assignment);
        if (file != null) {
            String path = storageService.store(file, assignment.getId());
            FileHistory fileHistory = new FileHistory();
            fileHistory.setFileName(file.getOriginalFilename());
            fileHistory.setModificationDate(new Date(System.currentTimeMillis()));
            fileHistory.setPath(path);
            fileHistory.setReferencedId(assignment.getId());
            fileHistory.setFileType(FileType.ASSIGNMENT.getName());

            fileHistoryRepository.save(fileHistory);
            assignment.setFilename(path);
            assignmentRepository.save(assignment);
        }

        return "redirect:/Assignment/Index";
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
    public String edit(@Valid Assignment assignment, @RequestParam MultipartFile file, BindingResult binding, Model model) {
        if (binding.hasErrors()) {
            return "redirect:/Assignment/Index";
        }

        if (assignment != null) {
            Assignment assignmentFromDb = assignmentRepository.getOne(assignment.getId());
            assignmentFromDb.setTitle(assignment.getTitle());
            assignmentFromDb.setContent(assignment.getContent());
            assignmentFromDb.setCourse(assignment.getCourse());
            assignmentFromDb.setTeacher(assignment.getTeacher());
            assignmentFromDb.setFilename(assignment.getFilename());

            storageService.delete(assignmentFromDb.getFilename());
            String path = storageService.store(file, assignment.getId());
            FileHistory fileHistory = new FileHistory();
            fileHistory.setFileName(file.getOriginalFilename());
            fileHistory.setModificationDate(new Date(System.currentTimeMillis()));
            fileHistory.setPath(path);
            fileHistory.setReferencedId(assignmentFromDb.getId());
            fileHistory.setFileType(FileType.ASSIGNMENT.getName());

            fileHistoryRepository.save(fileHistory);
            assignmentRepository.save(assignmentFromDb);
        }

        return "redirect:/Assignment/Index";
    }

    @GetMapping("/Delete")
    public String delete(Long id) {
        Assignment assignment = assignmentRepository.getOne(id);
        if (assignment != null) {
            assignmentRepository.delete(assignment);
        }

        return "redirect:/Assignment/Index";
    }

    private void fillModel(Model model, Assignment assignment, boolean isCreateAction) {
        model.addAttribute("assignment", assignment);
        model.addAttribute("isCreateAction", isCreateAction);
        model.addAttribute("teachers", teacherRepository.findAll());
        model.addAttribute("courses", courseRepository.findAll());
    }
}
