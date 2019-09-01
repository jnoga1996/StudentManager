package com.smanager.controllers;

import com.smanager.dao.models.FileHistory;
import com.smanager.dao.models.Solution;
import com.smanager.dao.repositories.*;
import com.smanager.storage.StorageService;
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

    private SolutionRepository solutionRepository;
    private TeacherRepository teacherRepository;
    private StudentRepository studentRepository;
    private FileHistoryRepository fileHistoryRepository;
    private AssignmentRepository assignmentRepository;
    private StorageService storageService;

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
    }

    @GetMapping("Index")
    public String index(Model model) {
        model.addAttribute("solutions", solutionRepository.findAll());
        Stream<Path> stream = storageService.loadAll();
        model.addAttribute("files", storageService.loadAll().map(
                path -> MvcUriComponentsBuilder.fromMethodName(FileUploadController.class,
                        "serveFile", path.getFileName().toString()).build().toString())
                .collect(Collectors.toList()));

        return "solution_index";
    }

    @GetMapping("/Create")
    public String showForm(Model model) {
        model.addAttribute("solution", new Solution());
        model.addAttribute("isCreate", true);
        model.addAttribute("teachers", teacherRepository.findAll());
        model.addAttribute("students", studentRepository.findAll());
        model.addAttribute("assignments", assignmentRepository.findAll());
        return "solution_form";
    }

    @PostMapping("/Create")
    public String create(@Valid Solution solution, @RequestParam MultipartFile file, Model model, BindingResult binding) {
        if (binding.hasErrors()) {
            return "solution_index";
        }

        solutionRepository.save(solution);
        if (file != null) {
            String path = storageService.storeSolution(file, solution.getId());
            FileHistory fileHistory = new FileHistory();
            fileHistory.setFileName(file.getOriginalFilename());
            fileHistory.setModificationDate(new Date(System.currentTimeMillis()));
            fileHistory.setPath(path);

            fileHistoryRepository.save(fileHistory);
            solution.setPath(path);
            solutionRepository.save(solution);
        }

        return "redirect:/Solution/Index";
    }

}
