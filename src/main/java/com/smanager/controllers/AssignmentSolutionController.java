package com.smanager.controllers;

import com.smanager.dao.models.Assignment;
import com.smanager.dao.models.Solution;
import com.smanager.dao.models.User;
import com.smanager.dao.repositories.AssignmentRepository;
import com.smanager.dao.repositories.SolutionRepository;
import com.smanager.interfaces.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

@Controller
@RequestMapping("/AssignmentSolution")
@PreAuthorize("hasAnyRole('TEACHER', 'ADMIN', 'STUDENT')")
public class AssignmentSolutionController {

    private AssignmentRepository assignmentRepository;
    private SolutionRepository solutionRepository;
    private IUserService userService;

    @Autowired
    public AssignmentSolutionController(AssignmentRepository assignmentRepository,
                                        SolutionRepository solutionRepository, IUserService userService) {
        this.assignmentRepository = assignmentRepository;
        this.solutionRepository = solutionRepository;
        this.userService = userService;
    }

    @GetMapping("Index")
    public String index(Model model) {
        List<Assignment> assignments = assignmentRepository.findAll();
        HashMap<Assignment, List<Solution>> assignmentSolutionsMap = new HashMap<>();

        for (Assignment as : assignments) {
            List<Solution> solutions = solutionRepository.findAllByAssignment_Id(as.getId());
            assignmentSolutionsMap.put(as, solutions);
        }

        fillModel(model, assignments, assignmentSolutionsMap);

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

        fillModel(model, assignments, assignmentSolutionsMap);

        return "assignmentSolution_index";
    }

    private void fillModel(Model model, List<Assignment> assignments,
                           HashMap<Assignment, List<Solution>> assignmentSolutionsMap) {
        User user = userService.getLoggedUser();
        model.addAttribute("assignmentSolutions", assignments);
        model.addAttribute("solutionsForAssignment", assignmentSolutionsMap);
        model.addAttribute("user", user);
    }
}
