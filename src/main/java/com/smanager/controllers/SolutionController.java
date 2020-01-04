package com.smanager.controllers;

import com.smanager.Cache;
import com.smanager.dao.models.Grades;
import com.smanager.dao.models.Solution;
import com.smanager.dao.models.Student;
import com.smanager.dao.models.User;
import com.smanager.dao.repositories.*;
import com.smanager.services.FileUploadHelper;
import com.smanager.services.UserService;
import com.smanager.storage.StorageService;
import com.smanager.utils.PaginationHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import javax.validation.Valid;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/Solution")
public class SolutionController {

    public final static String DETAILS_URL = "/Solution/Details";
    private final static String INDEX_REDIRECT_STRING = "redirect:/Solution/Index";

    private SolutionRepository solutionRepository;
    private TeacherRepository teacherRepository;
    private StudentRepository studentRepository;
    private AssignmentRepository assignmentRepository;

    private StorageService storageService;
    private FileUploadHelper fileUploadHelper;
    private UserService userService;
    private PaginationHelper<Solution> paginationHelper;

    @Autowired
    public SolutionController(SolutionRepository solutionRepository, TeacherRepository teacherRepository,
                              StudentRepository studentRepository, FileHistoryRepository fileHistoryRepository,
                              StorageService storageService, AssignmentRepository assignmentRepository,
                              UserService userService) {
        this.solutionRepository = solutionRepository;
        this.teacherRepository = teacherRepository;
        this.studentRepository = studentRepository;
        this.storageService = storageService;
        this.assignmentRepository = assignmentRepository;
        fileUploadHelper = new FileUploadHelper(fileHistoryRepository, storageService);
        this.userService = userService;
        paginationHelper = new PaginationHelper<>(solutionRepository);
    }

    @GetMapping("Index")
    public String index(Model model) {
        List<Solution> solutions = solutionRepository.findAll();
        fillModel(model, solutions);
        Cache.put(userService.getLoggedUser().getId(), "redirect:/Solution/Index");

        return "solution_index";
    }

    @GetMapping("Index/{page}")
    public String index(Model model, @PathVariable("page") int page) {
        Page<Solution> solutions = paginationHelper.getPage(page);
        fillModel(model, solutions);

        return "solution_index";
    }

    private void fillModel(Model model, Iterable<Solution> solutions) {
        User user = userService.getLoggedUser();
        updateLinks(solutions, storageService.loadAllByType(Solution.class).collect(Collectors.toList()));
        model.addAttribute("solutions", solutions);
        model.addAttribute("files", getAllFiles());

        model.addAttribute("user", user);
        model.addAttribute("pages", paginationHelper.getPageList());
    }

    private List<String> getAllFiles() {
        List<String> filePaths = new ArrayList<>();
        List<Path> paths = storageService.loadAllByType(Solution.class).collect(Collectors.toList());
        for (Path path : paths) {
            String filePath = MvcUriComponentsBuilder.fromMethodName(FileUploadController.class, "serveFile", path.getFileName().toString()).build().toString();
            filePaths.add(filePath);
        }
        return filePaths;
    }

    private void updateLinks(Iterable<Solution> solutions, List<Path> paths) {
        for (Solution solution : solutions) {
            for (Path path : paths) {
                if (solution.getPath() != null && solution.getPath().contains(path.getFileName().toString())) {
                    String filePath = MvcUriComponentsBuilder.fromMethodName(FileUploadController.class, "serveFile", path.getFileName().toString()).build().toString();
                    solution.setPath(filePath);
                }
            }
        }
    }

    @GetMapping("/Create")
    public String showForm(Model model) {
        User user = userService.getLoggedUser();
        fillModel(model, new Solution(), true);
        model.addAttribute("user", user);
        model.addAttribute("grade", Grades.NO_GRADE);

        return "solution_form";
    }

    @PostMapping("/Create")
    public String create(@Valid Solution solution, @RequestParam MultipartFile file, Model model, BindingResult binding) {
        User user = userService.getLoggedUser();
        if (binding.hasErrors()) {
            return "solution_index";
        }

        Long studentId = userService.getStudentOrTeacherId(user);
        Student student = studentRepository.getOne(studentId);
        solution.setStudent(student);
        fileUploadHelper.saveFileToRepository(solutionRepository, file, solution);
        model.addAttribute("user", user);

        return INDEX_REDIRECT_STRING;
    }

    @GetMapping("/Edit")
    public String edit(Model model, Long id) {
        User user = userService.getLoggedUser();
        Solution solution = solutionRepository.getOne(id);
        if (solution == null) {
            return INDEX_REDIRECT_STRING;
        }
        fillModel(model, solution, false);
        model.addAttribute("id", id);
        model.addAttribute("user", user);
        model.addAttribute("grade", Grades.NO_GRADE);

        return "solution_form";
    }

    @PostMapping("/Edit")
    public String edit(@Valid Solution solution, @RequestPart MultipartFile file, Model model, BindingResult binding) {
        User user = userService.getLoggedUser();
        if (binding.hasErrors()) {
            model.addAttribute("user", user);
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

        model.addAttribute("user", user);

        return INDEX_REDIRECT_STRING;
    }

    @GetMapping("/Details")
    public String details(Model model, Long id) {
        User user = userService.getLoggedUser();
        Solution solution = solutionRepository.getOne(id);
        if (solution == null) {
            return INDEX_REDIRECT_STRING;
        }
        fillModel(model, solution, false);
        model.addAttribute("user", user);

        return "solution_details";
    }

    @GetMapping("/Delete")
    public String delete(Model model, Long id) {
        User user = userService.getLoggedUser();
        Solution solution = solutionRepository.getOne(id);
        if (solution != null) {
            solutionRepository.delete(solution);
        }
        model.addAttribute("user", user);

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
