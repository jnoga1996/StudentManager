package com.smanager.controllers;

import com.smanager.dao.repositories.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/Teacher")
@PreAuthorize("hasRole('ADMIN', 'TEACHER')")
public class TeacherController {

    @Autowired
    private TeacherRepository teacherRepository;

    @GetMapping("Index")
    public String index(Model model) {
        model.addAttribute("teachers", teacherRepository.findAll());
        return "teacher_index";
    }
}
