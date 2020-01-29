package com.smanager.controllers;

import com.smanager.dao.models.User;
import com.smanager.dao.repositories.*;
import com.smanager.interfaces.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
@RequestMapping("/Student")
public class StudentController {

    private StudentRepository studentRepository;
    private IUserService userService;
    private UserRepository userRepository;

    @Autowired
    public StudentController(StudentRepository studentRepository, UserRepository userRepository, IUserService userService) {
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
