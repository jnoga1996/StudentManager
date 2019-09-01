package com.smanager.controllers;

import com.smanager.dao.repositories.FileHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/FileHistory")
public class FileHistoryController {

    private FileHistoryRepository fileHistoryRepository;

    @Autowired
    public FileHistoryController(FileHistoryRepository fileHistoryRepository) {
        this.fileHistoryRepository = fileHistoryRepository;
    }

    @GetMapping("Index")
    public String getHistory(Model model) {
        model.addAttribute("fileHistory", fileHistoryRepository.findAll());
        return "fileHistory_index";
    }

    @GetMapping("Indexo")
    public String getHistory(Model model, Long id) {
        model.addAttribute("fileHistory", fileHistoryRepository.findAllById(id));
        return "fileHistory_index";
    }
}
