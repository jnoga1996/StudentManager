package com.smanager.controllers;

import com.smanager.Bundles;
import com.smanager.dao.models.Bundle;
import com.smanager.dao.repositories.BundleRepository;
import com.smanager.dao.repositories.UserRepository;
import com.smanager.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
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

    @Autowired
    public HomeController(UserRepository userRepository, BundleRepository bundleRepository) {
        this.userRepository = userRepository;
        userService = new UserService(SecurityContextHolder.getContext().getAuthentication(), userRepository);
        bundles = new Bundles(bundleRepository);
    }

    @RequestMapping("/")
    public String index(Model model) {
        model.addAttribute("user", userService.getLoggedUser());

        return "home_index";
    }
}
