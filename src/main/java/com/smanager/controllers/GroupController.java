package com.smanager.controllers;

import com.smanager.dao.repositories.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/Group")
public class GroupController {

    @Autowired
    private GroupRepository groupRepository;

    @GetMapping("Index")
    public String index(Model model) {
        model.addAttribute("Groups", groupRepository.findAll());
        return "group_index";
    }
}
