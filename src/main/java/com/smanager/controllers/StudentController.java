package com.smanager.controllers;

import com.smanager.Bundles;
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
    private Bundles bundles;

    @Autowired
    public StudentController(StudentRepository studentRepository, UserRepository userRepository,
                             BundleRepository bundleRepository) {
        this.studentRepository = studentRepository;
        this.userRepository = userRepository;
        userService = new UserService(SecurityContextHolder.getContext().getAuthentication(), userRepository);
        bundles = new Bundles(bundleRepository);
    }

    @GetMapping("Index")
    public String index(Model model) {
        model.addAttribute("students", studentRepository.findAll());
        model.addAttribute("user", userService.getLoggedUser());
        return "student_index";
    }
}
