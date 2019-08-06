package com.smanager.controllers;

import com.smanager.dao.repositories.AssignmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/Assignment")
public class AssignmentController {

    @Autowired
    private AssignmentRepository assignmentRepository;

    @GetMapping("Index")
    public String index(Model model) {
        model.addAttribute("assignments", assignmentRepository.findAll());
        return "assignment_index";
    }
}
