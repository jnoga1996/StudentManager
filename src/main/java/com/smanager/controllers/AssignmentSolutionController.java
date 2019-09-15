package com.smanager.controllers;

import com.smanager.dao.models.Assignment;
import com.smanager.dao.models.Solution;
import com.smanager.dao.repositories.AssignmentRepository;
import com.smanager.dao.repositories.SolutionRepository;
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

    private AssignmentRepository assignmentRepository;
    private SolutionRepository solutionRepository;

    @Autowired
    public AssignmentSolutionController(AssignmentRepository assignmentRepository,
                                        SolutionRepository solutionRepository) {
        this.assignmentRepository = assignmentRepository;
        this.solutionRepository = solutionRepository;
    }

    @GetMapping("Index")
    public String index(Model model) {
        List<Assignment> assignments = assignmentRepository.findAll();
        HashMap<Assignment, List<Solution>> assignmentSolutionsMap = new HashMap<>();

        for (Assignment as : assignments) {
            List<Solution> solutions = solutionRepository.findAllByAssignment_Id(as.getId());
            assignmentSolutionsMap.put(as, solutions);
        }

        model.addAttribute("assignmentSolutions", assignments);
        model.addAttribute("solutionsForAssignment", assignmentSolutionsMap);
        return "assignmentSolution_index";
    }

    @GetMapping("/SolutionsToCheck")
    public String solutionsToCheck(Model model) {
        List<Assignment> assignments = new LinkedList<>();
        for (Assignment assignment : assignmentRepository.findAll()) {
            if (assignment.getSolutions().stream().anyMatch(s -> s.isFinished())) {
                assignments.add(assignment);
            }
        }
        HashMap<Assignment, List<Solution>> assignmentSolutionsMap = new HashMap<>();

        for (Assignment as : assignments) {
            List<Solution> solutions = solutionRepository.findAllByAssignment_IdAndFinishedTrue(as.getId());
            assignmentSolutionsMap.put(as, solutions);
        }

        model.addAttribute("assignmentSolutions", assignments);
        model.addAttribute("solutionsForAssignment", assignmentSolutionsMap);
        return "assignmentSolution_index";
    }
}
