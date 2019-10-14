package com.smanager.controllers;

import com.smanager.Bundles;
import com.smanager.dao.repositories.BundleRepository;
import com.smanager.dao.repositories.TeacherRepository;
import com.smanager.dao.repositories.UserRepository;
import com.smanager.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/Teacher")
@PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
public class TeacherController {

    private TeacherRepository teacherRepository;
    private UserService userService;
    private UserRepository userRepository;
    private Bundles bundles;

    @Autowired
    public TeacherController(TeacherRepository teacherRepository, UserRepository userRepository,
                             BundleRepository bundleRepository) {
        this.teacherRepository = teacherRepository;
        this.userRepository = userRepository;
        userService = new UserService(SecurityContextHolder.getContext().getAuthentication(), userRepository);
        bundles = new Bundles(bundleRepository);
    }

    @GetMapping("Index")
    public String index(Model model) {
        model.addAttribute("teachers", teacherRepository.findAll());
        model.addAttribute("user", userService.getLoggedUser());
        return "teacher_index";
    }
}
