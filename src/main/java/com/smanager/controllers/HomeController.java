package com.smanager.controllers;

import com.smanager.dao.repositories.UserRepository;
import com.smanager.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {

    private UserService userService;
    private UserRepository userRepository;

    @Autowired
    public HomeController(UserRepository userRepository) {
        this.userRepository = userRepository;
        userService = new UserService(SecurityContextHolder.getContext().getAuthentication(), userRepository);
    }

    @RequestMapping("/")
    public String index(Model model) {
        model.addAttribute("user", userService.getLoggedUser());
        return "home_index";
    }
}
