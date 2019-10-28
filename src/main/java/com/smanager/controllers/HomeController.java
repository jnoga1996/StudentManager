package com.smanager.controllers;

import com.smanager.Bundles;
import com.smanager.dao.models.Bundle;
import com.smanager.dao.repositories.AssignmentRepository;
import com.smanager.dao.repositories.BundleRepository;
import com.smanager.dao.repositories.CourseRepository;
import com.smanager.dao.repositories.UserRepository;
import com.smanager.services.UserService;
import com.smanager.utils.CourseHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Locale;

@Controller
public class HomeController {

    private UserService userService;
    private UserRepository userRepository;
    private Bundles bundles;
    private CourseHelper courseHelper;
    private CourseRepository courseRepository;

    @Autowired
    public HomeController(UserRepository userRepository, BundleRepository bundleRepository,
                          CourseRepository courseRepository, AssignmentRepository assignmentRepository) {
        this.userRepository = userRepository;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        userService = new UserService(authentication, userRepository);
        bundles = new Bundles(bundleRepository);
        this.courseRepository = courseRepository;
        courseHelper = new CourseHelper(courseRepository, assignmentRepository);
    }

    @RequestMapping("/")
    public String index(Model model) {
        boolean isLogged = false;
        if (userService.getLoggedUser() != null) {
            isLogged = true;
        }

        model.addAttribute("isLogged", isLogged);
        model.addAttribute("user", userService.getLoggedUser());
        model.addAttribute("courses", courseRepository.findAll());

        return "home_index";
    }
}
