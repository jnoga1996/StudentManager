package com.smanager.controllers;

import com.smanager.Bundles;
import com.smanager.dao.models.User;
import com.smanager.dao.repositories.AssignmentRepository;
import com.smanager.dao.repositories.BundleRepository;
import com.smanager.dao.repositories.CourseRepository;
import com.smanager.interfaces.IUserService;
import com.smanager.utils.CourseHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {

    private Bundles bundles;
    private CourseHelper courseHelper;
    private CourseRepository courseRepository;
    private IUserService userService;

    @Autowired
    public HomeController(BundleRepository bundleRepository, CourseRepository courseRepository,
                          AssignmentRepository assignmentRepository, IUserService userService) {
        bundles = new Bundles(bundleRepository);
        this.courseRepository = courseRepository;
        courseHelper = new CourseHelper(courseRepository, assignmentRepository);
        this.userService = userService;
    }

    @RequestMapping("/")
    public String index(Model model) {
        boolean isLogged = false;
        User user = userService.getLoggedUser();
        if (user != null) {
            isLogged = true;
        }

        model.addAttribute("isLogged", isLogged);
        model.addAttribute("user", user);
        model.addAttribute("courses", courseRepository.findAll());

        if (user != null && user.isStudent()) {
            return "redirect:/Work/Index";
        } else if (user != null && user.isTeacher()) {
            return "redirect:/Work/TeacherWork";
        }

        return "login";
    }

}
