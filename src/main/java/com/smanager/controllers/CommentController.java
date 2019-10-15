package com.smanager.controllers;

import com.smanager.Bundles;
import com.smanager.WebSecurityConfig;
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
    private UserRepository userRepository;
    private StudentRepository studentRepository;
    private TeacherRepository teacherRepository;
    private UserService userService;

    @Autowired
    public CommentController(CommentRepository commentRepository, SolutionRepository solutionRepository,
                             UserRepository userRepository, StudentRepository studentRepository,
                             TeacherRepository teacherRepository) {
        this.commentRepository = commentRepository;
        this.solutionRepository = solutionRepository;
        this.userRepository = userRepository;
        this.userService = new UserService(SecurityContextHolder.getContext().getAuthentication(), userRepository);
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
    }

    @GetMapping("/Index")
    public String getCommentsForSolution(Model model, Long solutionId) {
        List<Comment> comments = commentRepository.findAllBySolutionIdOrderByCreationDateDesc(solutionId);
        model.addAttribute("comments", comments);
        model.addAttribute("user", userService.getLoggedUser());

        return "comment_index";
    }

    @GetMapping("/Create")
    public String createComment(Model model, Long solutionId) {
        Solution solution = solutionRepository.getOne(solutionId);
        User user = userService.getLoggedUser();
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
        if (result.hasErrors()) {
            return "comment_form";
        }

        User user = userService.getLoggedUser();
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
        model.addAttribute("user", userService.getLoggedUser());

        return INDEX_RETURN_STRING;
    }

}
