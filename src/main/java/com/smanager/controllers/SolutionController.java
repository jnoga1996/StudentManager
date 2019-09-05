package com.smanager.controllers;

import com.smanager.dao.models.FileHistory;
import com.smanager.dao.models.Solution;
import com.smanager.dao.repositories.*;
import com.smanager.services.FileUploadHelper;
import com.smanager.storage.StorageService;
import com.sun.xml.internal.bind.v2.TODO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import javax.validation.Valid;
import java.nio.file.Path;
import java.util.Date;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
@RequestMapping("/Solution")
public class SolutionController {

    private final static String INDEX_REDIRECT_STRING = "redirect:/Solution/Index";

    private SolutionRepository solutionRepository;
    private TeacherRepository teacherRepository;
    private StudentRepository studentRepository;
    private FileHistoryRepository fileHistoryRepository;
    private AssignmentRepository assignmentRepository;
    private StorageService storageService;
    private FileUploadHelper fileUploadHelper;

    @Autowired
    public SolutionController(SolutionRepository solutionRepository, TeacherRepository teacherRepository,
                              StudentRepository studentRepository, FileHistoryRepository fileHistoryRepository,
                              StorageService storageService, AssignmentRepository assignmentRepository) {
        this.solutionRepository = solutionRepository;
        this.teacherRepository = teacherRepository;
        this.studentRepository = studentRepository;
        this.fileHistoryRepository = fileHistoryRepository;
        this.storageService = storageService;
        this.assignmentRepository = assignmentRepository;
        fileUploadHelper = new FileUploadHelper(fileHistoryRepository, storageService);
    }

    @GetMapping("Index")
    public String index(Model model) {
        model.addAttribute("solutions", solutionRepository.findAll());
        Stream<Path> stream = storageService.loadAll();
        model.addAttribute("files", storageService.loadAllByType(Solution.class).map(
                path -> MvcUriComponentsBuilder.fromMethodName(FileUploadController.class,
                        "serveFile", path.getFileName().toString()).build().toString())
                .collect(Collectors.toList()));

        return "solution_index";
    }

    @GetMapping("/Create")
    public String showForm(Model model) {
        fillModel(model, new Solution(), true);
        return "solution_form";
    }

    @PostMapping("/Create")
    public String create(@Valid Solution solution, @RequestParam MultipartFile file, Model model, BindingResult binding) {
        if (binding.hasErrors()) {
            return "solution_index";
        }

        fileUploadHelper.saveFileToRepository(solutionRepository, file, solution);

        return INDEX_REDIRECT_STRING;
    }

    @GetMapping("/Edit")
    public String edit(Model model, Long id) {
        Solution solution = solutionRepository.getOne(id);
        if (solution == null) {
            return INDEX_REDIRECT_STRING;
        }
        fillModel(model, solution, false);
        model.addAttribute("id", id);

        return "solution_form";
    }

    @PostMapping("/Edit")
    public String edit(@Valid Solution solution, @RequestPart MultipartFile file, Model model, BindingResult binding) {
        if (binding.hasErrors()) {
            return INDEX_REDIRECT_STRING;
        }

        if (solution != null) {
            Solution solutionFromDb = solutionRepository.getOne(solution.getId());
            solutionFromDb.setStudent(solution.getStudent());
            solutionFromDb.setContent(solution.getContent());
            solutionFromDb.setAssignment(solution.getAssignment());
            solutionFromDb.setGrade(solution.getGrade());

            fileUploadHelper.updateFileHistory(solutionFromDb, file);
            solutionRepository.save(solutionFromDb);
        }

        return INDEX_REDIRECT_STRING;
    }

    @GetMapping("/Delete")
    public String delete(Model model, Long id) {
        Solution solution = solutionRepository.getOne(id);
        if (solution != null) {
            solutionRepository.delete(solution);
        }

        return INDEX_REDIRECT_STRING;
    }

    private void fillModel(Model model, Solution solution, boolean isCreate) {
        model.addAttribute("solution", solution);
        model.addAttribute("isCreate", isCreate);
        model.addAttribute("teachers", teacherRepository.findAll());
        model.addAttribute("students", studentRepository.findAll());
        model.addAttribute("assignments", assignmentRepository.findAll());
    }

}
