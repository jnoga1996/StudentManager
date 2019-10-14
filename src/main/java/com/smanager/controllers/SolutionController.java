package com.smanager.controllers;

import com.smanager.Bundles;
import com.smanager.dao.models.Grades;
import com.smanager.dao.models.Solution;
import com.smanager.dao.models.User;
import com.smanager.dao.repositories.*;
import com.smanager.services.FileUploadHelper;
import com.smanager.services.UserService;
import com.smanager.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/Solution")
public class SolutionController {

    private final static String INDEX_REDIRECT_STRING = "redirect:/Solution/Index";

    private SolutionRepository solutionRepository;
    private TeacherRepository teacherRepository;
    private StudentRepository studentRepository;
    private FileHistoryRepository fileHistoryRepository;
    private AssignmentRepository assignmentRepository;
    private UserRepository userRepository;

    private StorageService storageService;
    private FileUploadHelper fileUploadHelper;
    private UserService userService;
    private Bundles bundles;

    @Autowired
    public SolutionController(SolutionRepository solutionRepository, TeacherRepository teacherRepository,
                              StudentRepository studentRepository, FileHistoryRepository fileHistoryRepository,
                              StorageService storageService, AssignmentRepository assignmentRepository,
                              UserRepository userRepository, BundleRepository bundleRepository) {
        this.solutionRepository = solutionRepository;
        this.teacherRepository = teacherRepository;
        this.studentRepository = studentRepository;
        this.fileHistoryRepository = fileHistoryRepository;
        this.userRepository = userRepository;
        this.storageService = storageService;
        this.assignmentRepository = assignmentRepository;
        fileUploadHelper = new FileUploadHelper(fileHistoryRepository, storageService);
        userService = new UserService(SecurityContextHolder.getContext().getAuthentication(), userRepository);
        bundles = new Bundles(bundleRepository);
    }

    @GetMapping("Index")
    public String index(Model model) {
        User user = userService.getLoggedUser();
        model.addAttribute("solutions", solutionRepository.findAll());
        model.addAttribute("files", storageService.loadAllByType(Solution.class).map(
                path -> MvcUriComponentsBuilder.fromMethodName(FileUploadController.class,
                        "serveFile", path.getFileName().toString()).build().toString())
                .collect(Collectors.toList()));

        model.addAttribute("user", user);

        return "solution_index";
    }

    @GetMapping("/Create")
    public String showForm(Model model) {
        fillModel(model, new Solution(), true);
        model.addAttribute("user", userService.getLoggedUser());
        model.addAttribute("grade", Grades.NO_GRADE);

        return "solution_form";
    }

    @PostMapping("/Create")
    public String create(@Valid Solution solution, @RequestParam MultipartFile file, Model model, BindingResult binding) {
        if (binding.hasErrors()) {
            return "solution_index";
        }

        fileUploadHelper.saveFileToRepository(solutionRepository, file, solution);
        model.addAttribute("user", userService.getLoggedUser());

        return INDEX_REDIRECT_STRING;
    }

    @GetMapping("/Edit")
    public String edit(Model model, Long id) {
        Solution solution = solutionRepository.getOne(id);
        if (solution == null) {
            return INDEX_REDIRECT_STRING;
        }
        fillModel(model, solution, false);
        model.addAttribute("id", id);
        model.addAttribute("user", userService.getLoggedUser());
        model.addAttribute("grade", Grades.NO_GRADE);

        return "solution_form";
    }

    @PostMapping("/Edit")
    public String edit(@Valid Solution solution, @RequestPart MultipartFile file, Model model, BindingResult binding) {
        if (binding.hasErrors()) {
            model.addAttribute("user", userService.getLoggedUser());
            return INDEX_REDIRECT_STRING;
        }

        if (solution != null) {
            Solution solutionFromDb = solutionRepository.getOne(solution.getId());
            solutionFromDb.setStudent(solution.getStudent());
            solutionFromDb.setContent(solution.getContent());
            solutionFromDb.setAssignment(solution.getAssignment());
            solutionFromDb.setGrade(solution.getGrade());
            solutionFromDb.setFinished(solution.isFinished());
            solutionFromDb.setCreationDate(LocalDateTime.now());

            fileUploadHelper.updateFileHistory(solutionFromDb, file);

            solutionRepository.save(solutionFromDb);
        }

        model.addAttribute("user", userService.getLoggedUser());

        return INDEX_REDIRECT_STRING;
    }

    @GetMapping("/Delete")
    public String delete(Model model, Long id) {
        Solution solution = solutionRepository.getOne(id);
        if (solution != null) {
            solutionRepository.delete(solution);
        }
        model.addAttribute("user", userService.getLoggedUser());

        return INDEX_REDIRECT_STRING;
    }

    private void fillModel(Model model, Solution solution, boolean isCreate) {
        model.addAttribute("solution", solution);
        model.addAttribute("isCreate", isCreate);
        model.addAttribute("teachers", teacherRepository.findAll());
        model.addAttribute("students", studentRepository.findAll());
        model.addAttribute("assignments", assignmentRepository.findAll());
        model.addAttribute("grades", Arrays.asList(Grades.values()));
        model.addAttribute("creationDate", LocalDateTime.now());
    }

}
