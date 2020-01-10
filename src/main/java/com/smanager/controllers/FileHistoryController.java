package com.smanager.controllers;

import com.smanager.dao.repositories.FileHistoryRepository;
import com.smanager.dao.repositories.UserRepository;
import com.smanager.interfaces.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/FileHistory")
public class FileHistoryController {

    private FileHistoryRepository fileHistoryRepository;
    private UserRepository userRepository;
    private IUserService userService;

    @Autowired
    public FileHistoryController(FileHistoryRepository fileHistoryRepository, UserRepository userRepository, IUserService userService) {
        this.fileHistoryRepository = fileHistoryRepository;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @GetMapping("Index")
    public String getHistory(Model model) {
        model.addAttribute("fileHistory", fileHistoryRepository.findAll());
        model.addAttribute("user", userService.getLoggedUser());

        return "fileHistory_index";
    }

    @GetMapping("Indexo")
    public String getHistory(Model model, Long id) {
        model.addAttribute("fileHistory", fileHistoryRepository.findAllById(id));
        model.addAttribute("user", userService.getLoggedUser());

        return "fileHistory_index";
    }
}
