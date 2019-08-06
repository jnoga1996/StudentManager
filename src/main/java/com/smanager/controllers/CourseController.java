package com.smanager.controllers;

import com.smanager.dao.models.Course;
import com.smanager.dao.repositories.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Controller
@RequestMapping("/Course")
public class CourseController {

    @Autowired
    private CourseRepository courseRepository;

    @GetMapping("Index")
    public String index(Model model) {
        model.addAttribute("courses", courseRepository.findAll());
        return "course_index";
    }

    @GetMapping("/Create")
    public String showForm(Course course) {
        return "course_form";
    }

    @PostMapping("/Create")
    public String create(@Valid Course course, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "course_form";
        }
        courseRepository.save(course);

        return "redirect:/Course/Index";
    }
}
