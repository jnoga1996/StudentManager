package com.smanager.controllers;

import com.smanager.dao.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/Student")
public class StudentController {

    @Autowired
    private StudentRepository studentRepository;

    @GetMapping("Index")
    public String index(Model model) {
        model.addAttribute("students", studentRepository.findAll());
        return "student_index";
    }
}
