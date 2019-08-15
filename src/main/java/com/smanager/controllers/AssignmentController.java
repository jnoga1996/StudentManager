package com.smanager.controllers;


import com.smanager.dao.models.Assignment;
import com.smanager.dao.repositories.AssignmentRepository;
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

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Controller
@RequestMapping("/Assignment")
public class AssignmentController {
    private static final String path = "/upload-dir/";

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private StorageService storageService;

    @Autowired
    private HttpServletRequest request;

    @GetMapping("Index")
    public String index(Model model) {
        model.addAttribute("assignments", assignmentRepository.findAll());
        return "assignment_index";
    }

    @GetMapping("Create")
    public String showForm(Model model) {
        model.addAttribute("assignment", new Assignment());
        return "assignment_form";
    }

    @PostMapping("Create")
    public String create(@Valid Assignment assignment, @RequestParam MultipartFile file, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "assignment_form";
        }

        assignmentRepository.save(assignment);
        if (file != null) {
            storageService.store(file, assignment.getId());
        }


        return "redirect:/Assignment/Index";
    }
}
