package com.smanager.controllers;

import com.smanager.dao.repositories.SolutionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/Solution")
public class SolutionController {

    @Autowired
    private SolutionRepository solutionRepository;

    @GetMapping("Index")
    public String index(Model model) {
        model.addAttribute("solutions", solutionRepository.findAll());
        return "solution_index";
    }
}
