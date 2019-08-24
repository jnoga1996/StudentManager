package com.smanager.controllers;

import com.smanager.dao.models.Assignment;
import com.smanager.dao.models.AssignmentSolution;
import com.smanager.dao.models.Course;
import com.smanager.dao.models.Solution;
import com.smanager.dao.repositories.AssignmentRepository;
import com.smanager.dao.repositories.AssignmentSolutionRepository;
import com.smanager.dao.repositories.CourseRepository;
import com.smanager.dao.repositories.SolutionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Controller
@RequestMapping("/Course")
public class CourseController {

    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private AssignmentRepository assignmentRepository;
    @Autowired
    private AssignmentSolutionRepository assignmentSolutionRepository;
    @Autowired
    private SolutionRepository solutionRepository;

    @GetMapping("Index")
    public String index(Model model) {
        model.addAttribute("courses", courseRepository.findAll());
        return "course_index";
    }

    @GetMapping("/Create")
    public String showForm(Course course, Model model) {
        model.addAttribute("isCreate", true);
        return "course_form";
    }

    @PostMapping("/Create")
    public String create(@Valid Course course, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "course_form";
        }
        courseRepository.save(course);

        return "redirect:/Course/Index";
    }

    @GetMapping("/Edit")
    public String edit(Model model, Long id) {
        Course course = courseRepository.getOne(id);
        if (course == null) {
            return "redirect:/Course/Index";
        }
        model.addAttribute("course", course);
        model.addAttribute("isCreate", false);
        model.addAttribute("id", id);
        return "course_form";
    }

    @PostMapping("/Edit")
    public String edit(@Valid Course course, Model model, BindingResult binding) {
        if (binding.hasErrors()) {
            return "redirect:/Course/Index";
        }

        Course courseFromDb = courseRepository.getOne(course.getId());
        courseFromDb.setTitle(course.getTitle());
        courseFromDb.setEcts(course.getEcts());
        courseRepository.save(courseFromDb);

        return "redirect:/Course/Index";
    }

    @GetMapping("/Delete")
    public String delete(Long id) {
        Course course = courseRepository.getOne(id);
        if (course != null) {
            for (Assignment assignment : course.getAssignments()) {
                System.out.println(String.format("Removed assignment %s", assignment));
                assignmentRepository.delete(assignment);
                for (AssignmentSolution as : assignment.getSolutions()) {
                    System.out.println(String.format("Removed assignment_solution %s", as));
                    assignmentSolutionRepository.delete(as);
                    Solution solution = solutionRepository.getOne(as.getSolution().getId());
                    System.out.println(String.format("Removed solution %s", solution));
                    solutionRepository.delete(solution);
                }
            }
            courseRepository.delete(course);
        }

        return "redirect:/Course/Index";
    }
}
