package com.smanager.controllers;


import com.smanager.dao.models.Assignment;
import com.smanager.dao.models.FileHistory;
import com.smanager.dao.repositories.AssignmentRepository;
import com.smanager.dao.repositories.FileHistoryRepository;
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
@RequestMapping("/Assignment")
public class AssignmentController {

    private AssignmentRepository assignmentRepository;
    private FileHistoryRepository fileHistoryRepository;
    private StorageService storageService;

    @Autowired
    public AssignmentController(AssignmentRepository assignmentRepository,
                                FileHistoryRepository fileHistoryRepository,
                                StorageService storageService) {
        this.assignmentRepository = assignmentRepository;
        this.fileHistoryRepository = fileHistoryRepository;
        this.storageService = storageService;
    }

    @GetMapping("Index")
    public String index(Model model) {
        model.addAttribute("assignments", assignmentRepository.findAll());
        return "assignment_index";
    }

    @GetMapping("Create")
    public String showForm(Model model) {
        model.addAttribute("assignment", new Assignment());
        model.addAttribute("isCreateAction", true);
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

            fileHistoryRepository.save(fileHistory);
        }

        return "redirect:/Assignment/Index";
    }

    @GetMapping("/Edit")
    public String edit(Model model, Long id) {
        Assignment assignment = assignmentRepository.getOne(id);
        if (assignment == null) {
            return "redirect:/Assignment/Index";
        }
        model.addAttribute("assignment", assignment);
        model.addAttribute("id", id);
        model.addAttribute("isCreateAction", false);

        return "assignment_form";
    }

    @PostMapping("/Edit")
    public String edit(@Valid Assignment assignment, BindingResult bindingResult, Model model) {
        return "";
    }
}
