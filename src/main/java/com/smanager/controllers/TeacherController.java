package com.smanager.controllers;

import com.smanager.dao.repositories.TeacherRepository;
import com.smanager.dao.repositories.UserRepository;
import com.smanager.interfaces.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/Teacher")
@PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
public class TeacherController {

    private TeacherRepository teacherRepository;
    private IUserService userService;
    private UserRepository userRepository;

    @Autowired
    public TeacherController(TeacherRepository teacherRepository, UserRepository userRepository, IUserService userService) {
        this.teacherRepository = teacherRepository;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @GetMapping("Index")
    public String index(Model model) {
        model.addAttribute("teachers", teacherRepository.findAll());
        model.addAttribute("user", userService.getLoggedUser());
        return "teacher_index";
    }
}
