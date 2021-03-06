package com.smanager.controllers;

import com.smanager.Cache;
import com.smanager.dao.models.*;
import com.smanager.dao.repositories.*;
import com.smanager.interfaces.IUserService;
import com.smanager.services.FileUploadHelper;
import com.smanager.services.RedirectService;
import com.smanager.storage.StorageService;
import com.smanager.utils.SolutionPaginationHelper;
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
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/Solution")
public class SolutionController {

    public final static String DETAILS_URL = "/Solution/Details";
    public final static String INDEX_REDIRECT_STRING = "redirect:/Solution/Index";

    private SolutionRepository solutionRepository;
    private TeacherRepository teacherRepository;
    private StudentRepository studentRepository;
    private AssignmentRepository assignmentRepository;
    private RedirectService redirectService;

    private StorageService storageService;
    private FileUploadHelper fileUploadHelper;
    private IUserService userService;
    private SolutionPaginationHelper paginationHelper;

    private String entryId;

    @Autowired
    public SolutionController(SolutionRepository solutionRepository, TeacherRepository teacherRepository,
                              StudentRepository studentRepository, FileHistoryRepository fileHistoryRepository,
                              StorageService storageService, AssignmentRepository assignmentRepository,
                              IUserService userService, RedirectService redirectService) {
        this.solutionRepository = solutionRepository;
        this.teacherRepository = teacherRepository;
        this.studentRepository = studentRepository;
        this.storageService = storageService;
        this.assignmentRepository = assignmentRepository;
        fileUploadHelper = new FileUploadHelper(fileHistoryRepository, storageService);
        this.userService = userService;
        paginationHelper = new SolutionPaginationHelper(solutionRepository);
        this.redirectService = redirectService;
    }

    @GetMapping("Index")
    public String index(Model model) {
        User user = userService.getLoggedUser();
        Page<Solution> solutions = paginationHelper.getPage(0, user);
        fillModel(model, solutions);
        Cache.put(userService.getLoggedUser().getId(), "redirect:/Solution/Index");

        return "solution_index";
    }

    @GetMapping("Index/{page}")
    public String index(Model model, @PathVariable("page") int page) {
        User user = userService.getLoggedUser();
        Page<Solution> solutions = paginationHelper.getPage(page, user);
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
                if (solution.getPath() != null && path.getFileName().toString().contains(solution.getPath())) {
                    String filePath = MvcUriComponentsBuilder.fromMethodName(FileUploadController.class, "serveFile", path.getFileName().toString()).build().toString();
                    solution.setPath(filePath);
                }
            }
        }
    }

    @GetMapping("/Create")
    public String showForm(Model model, @RequestParam(name = "courseId", required = false) Long courseId,
                           @RequestParam(name = "assignmentId", required = false) Long assignmentId) {
        User user = userService.getLoggedUser();
        fillModel(model, new Solution(), true, courseId);
        model.addAttribute("providedAssignmentId", assignmentId);
        if (assignmentId != null) {
            Assignment providedAssignment = assignmentRepository.getOne(assignmentId);
            model.addAttribute("providedAssignment", providedAssignment);
        }
        model.addAttribute("user", user);
        model.addAttribute("grade", Grades.NO_GRADE);

        return "solution_form";
    }

    @PostMapping("/Create")
    public String create(@Valid Solution solution, @RequestParam MultipartFile file, Model model, BindingResult binding,
                         @RequestParam(name = "providedAssignmentId", required = false) Long providedAssignmentId) {
        User user = userService.getLoggedUser();
        if (binding.hasErrors()) {
            return "solution_index";
        }

        Long studentId = userService.getStudentOrTeacherId(user);
        Student student = studentRepository.getOne(studentId);
        if (providedAssignmentId != null) {
            Assignment providedAssignment = assignmentRepository.getOne(providedAssignmentId);
            solution.setAssignment(providedAssignment);
        }
        solution.setStudent(student);
        solution.setPath("");
        fileUploadHelper.saveFileToRepository(solutionRepository, file, solution);
        model.addAttribute("user", user);

        return redirectService.getSolutionRedirectWorkUrl(user);
    }

    @GetMapping("/Edit")
    public String edit(Model model, Long id, @RequestParam(name = "entryId", required = false) String entryId) {
        User user = userService.getLoggedUser();
        Solution solution = solutionRepository.getOne(id);
        if (solution == null) {
            return INDEX_REDIRECT_STRING;
        }
        fillModel(model, solution, false);
        model.addAttribute("id", id);
        model.addAttribute("user", user);
        model.addAttribute("grade", Grades.NO_GRADE);

        if (solution.getPath() != null) {
            Path path = Paths.get(solution.getPath());
            String filePath = MvcUriComponentsBuilder.fromMethodName(FileUploadController.class,
                    "serveFile", path.getFileName().toString()).build().toString();
            model.addAttribute("uploadedFile", filePath);
        }

        if (entryId != null) {
            this.entryId = entryId;
        }

        return "solution_form";
    }

    @PostMapping("/Edit")
    public String edit(@Valid Solution solution, @RequestPart MultipartFile file, Model model,
                       BindingResult binding, @RequestParam(name = "removeFile", required = false) boolean removeFile) {
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

            if (removeFile) {
                storageService.delete(solutionFromDb.getPath());
                solutionFromDb.setPath(null);
                if (!file.isEmpty())
                    fileUploadHelper.updateFileHistory(solutionFromDb, file);
            } else if (!removeFile && !file.isEmpty() && solutionFromDb.getPath() == null) {
                fileUploadHelper.updateFileHistory(solutionFromDb, file);
            }

            solutionRepository.save(solutionFromDb);
        }

        model.addAttribute("user", user);

        return redirectService.getSolutionRedirectWorkUrlAndScrollToSelectedId(user, this.entryId);
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

    private void fillModel(Model model, Solution solution, boolean isCreate, Long courseId) {
        fillModel(model, solution, isCreate);
        List<Assignment> assignments;
        if (courseId == null) {
            assignments = assignmentRepository.findAll();
        } else {
            assignments = assignmentRepository.findAllByCourseId(courseId);
        }
        model.addAttribute("assignments", assignments);
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
