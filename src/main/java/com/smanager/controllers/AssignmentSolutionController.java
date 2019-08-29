package com.smanager.controllers;

import com.smanager.dao.models.Assignment;
import com.smanager.dao.models.AssignmentSolution;
import com.smanager.dao.models.Solution;
import com.smanager.dao.repositories.AssignmentSolutionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

@Controller
@RequestMapping("/AssignmentSolution")
public class AssignmentSolutionController {

    @Autowired
    private AssignmentSolutionRepository assignmentSolutionRepository;

    @GetMapping("Index")
    public String index(Model model) {
        List<AssignmentSolution> assignmentSolutionList = assignmentSolutionRepository.findAll();
        HashMap<Assignment, List<Solution>> assignmentSolutionsMap = new HashMap<>();
        List<AssignmentSolution> distinctAssignmentSolutionList = new LinkedList<>();

        for (AssignmentSolution asol : assignmentSolutionList) {
            if (!assignmentSolutionsMap.containsKey(asol.getAssignment())) {
                List<Solution> solutions = new LinkedList<>();
                solutions.add(asol.getSolution());
                assignmentSolutionsMap.put(asol.getAssignment(), solutions);
                distinctAssignmentSolutionList.add(asol);
            } else {
                assignmentSolutionsMap.get(asol.getAssignment()).add(asol.getSolution());
            }
        }
        
        model.addAttribute("assignmentSolutions", distinctAssignmentSolutionList);
        model.addAttribute("solutionsForAssignment", assignmentSolutionsMap);
        return "assignmentSolution_index";
    }
}
