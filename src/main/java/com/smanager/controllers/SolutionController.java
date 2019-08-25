package com.smanager.controllers;

import com.smanager.dao.models.FileHistory;
import com.smanager.dao.models.Solution;
import com.smanager.dao.repositories.FileHistoryRepository;
import com.smanager.dao.repositories.SolutionRepository;
import com.smanager.dao.repositories.StudentRepository;
import com.smanager.dao.repositories.TeacherRepository;
import com.smanager.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.Date;

@Controller
@RequestMapping("/Solution")
public class SolutionController {

    private SolutionRepository solutionRepository;
    private TeacherRepository teacherRepository;
    private StudentRepository studentRepository;
    private FileHistoryRepository fileHistoryRepository;
    private StorageService storageService;

    @Autowired
    public SolutionController(SolutionRepository solutionRepository, TeacherRepository teacherRepository,
                              StudentRepository studentRepository, FileHistoryRepository fileHistoryRepository,
                              StorageService storageService) {
        this.solutionRepository = solutionRepository;
        this.teacherRepository = teacherRepository;
        this.studentRepository = studentRepository;
        this.fileHistoryRepository = fileHistoryRepository;
        this.storageService = storageService;
    }

    @GetMapping("Index")
    public String index(Model model) {
        model.addAttribute("solutions", solutionRepository.findAll());
        return "solution_index";
    }

    @GetMapping("/Create")
    public String showForm(Model model) {
        model.addAttribute("solution", new Solution());
        model.addAttribute("isCreate", true);
        model.addAttribute("teachers", teacherRepository.findAll());
        model.addAttribute("students", studentRepository.findAll());
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
        }

        return "redirect:/Solution/Index";
    }
}
