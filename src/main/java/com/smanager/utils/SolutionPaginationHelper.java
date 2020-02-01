package com.smanager.utils;

import com.smanager.dao.models.Solution;
import com.smanager.dao.models.User;
import com.smanager.dao.repositories.SolutionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public class SolutionPaginationHelper extends PaginationHelper<Solution> {

    private SolutionRepository solutionRepository;

    @Autowired
    public SolutionPaginationHelper(JpaRepository repository, SolutionRepository solutionRepository) {
        super(repository);
        this.solutionRepository = solutionRepository;
    }

    public SolutionPaginationHelper(SolutionRepository solutionRepository) {
        super(solutionRepository);
        this.solutionRepository = solutionRepository;
    }

    public Page<Solution> getPage(int index, User user) {
        Long count = repository.count();
        Long availablePages = count / PAGE_SIZE;
        Pageable page;
        if (index > availablePages) {
            page = PageRequest.of(1, PAGE_SIZE);
        } else {
            page = PageRequest.of(index, PAGE_SIZE);
        }

        return getSolutions(user, page);
    }

    public List<Solution> getAll(User user) {
        return getSolutions(user);
    }

    public List<Solution> getSolutions(User user) {
        List<Solution> solutions;
        if (user.isStudent()) {
            solutions = solutionRepository.findAllStudentSolutions(user.getStudentUser().getId());
        } else if (user.isTeacher()) {
            solutions = solutionRepository.findAllStudentSolutionsForTeacher(user.getTeacherUser().getId());
        } else {
            solutions = solutionRepository.findAll();
        }
        return solutions;
    }

    public Page<Solution> getSolutions(User user, Pageable pageable) {
        Page<Solution> solutions;
        if (user.isStudent()) {
            solutions = solutionRepository.findAllStudentSolutions(user.getStudentUser().getId(), pageable);
        } else if (user.isTeacher()) {
            solutions = solutionRepository.findAllStudentSolutionsForTeacher(user.getTeacherUser().getId(), pageable);
        } else {
            solutions = solutionRepository.findAll(pageable);
        }
        return solutions;
    }
}
