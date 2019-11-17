package com.smanager.controllers;

import com.smanager.Bundles;
import com.smanager.dao.models.User;
import com.smanager.dao.repositories.*;
import com.smanager.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/Student")
public class StudentController {

    private StudentRepository studentRepository;
    private UserService userService;
    private UserRepository userRepository;

    @Autowired
    public StudentController(StudentRepository studentRepository, UserRepository userRepository, UserService userService) {
        this.studentRepository = studentRepository;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @GetMapping("Index")
    public String index(Model model) {
        User user = userService.getLoggedUser();
        model.addAttribute("students", studentRepository.findAll());
        model.addAttribute("user", user);
        return "student_index";
    }
}
