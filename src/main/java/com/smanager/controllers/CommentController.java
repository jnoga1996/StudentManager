package com.smanager.controllers;

import com.smanager.dao.models.Comment;
import com.smanager.dao.models.Solution;
import com.smanager.dao.models.User;
import com.smanager.dao.repositories.*;
import com.smanager.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/Comment")
public class CommentController {

    private static final String INDEX_RETURN_STRING = "redirect:/Comment/Index";

    private CommentRepository commentRepository;
    private SolutionRepository solutionRepository;
    private UserService userService;

    @Autowired
    public CommentController(CommentRepository commentRepository, SolutionRepository solutionRepository,
                             UserService userService) {
        this.commentRepository = commentRepository;
        this.solutionRepository = solutionRepository;
        this.userService = userService;
    }

    @GetMapping("/Index")
    public String getCommentsForSolution(Model model, Long solutionId) {
        User user = userService.getLoggedUser();
        List<Comment> comments = commentRepository.findAllBySolutionIdOrderByCreationDateDesc(solutionId);
        model.addAttribute("comments", comments);
        model.addAttribute("user", user);

        return "comment_index";
    }

    @GetMapping("/Create")
    public String createComment(Model model, Long solutionId) {
        User user = userService.getLoggedUser();
        Solution solution = solutionRepository.getOne(solutionId);
        if (solution == null || user == null) {
            return INDEX_RETURN_STRING;
        }
        Comment comment = new Comment();
        model.addAttribute("comment", comment);
        model.addAttribute("user", user);
        model.addAttribute("solutionId", solutionId);

        return "comment_form";
    }

    @PostMapping("/Create")
    public String createComment(@Valid Comment comment, @RequestParam("solutionId") Long solutionId, BindingResult result, Model model) {
        User user = userService.getLoggedUser();
        if (result.hasErrors()) {
            return "comment_form";
        }

        if (user.isStudent()) {
            comment.setStudent(user.getStudentUser());
        } else {
            comment.setTeacher(user.getTeacherUser());
        }
        comment.setCreationDate(LocalDateTime.now());

        Solution solution = solutionRepository.getOne(solutionId);
        if (solution == null) {
            return "comment_form";
        }
        comment.setSolution(solution);
        commentRepository.save(comment);
        model.addAttribute("user", user);

        return INDEX_RETURN_STRING;
    }

}
