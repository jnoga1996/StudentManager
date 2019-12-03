package com.smanager.utils;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;
import java.util.List;

public class PaginationHelper <T> {
    private JpaRepository repository;
    public final static int PAGE_SIZE = 3;

    public PaginationHelper(JpaRepository repository) {
        this.repository = repository;
    }

    public Page<T> getPage(int index) {
        Long count = repository.count();
        Long availablePages = count / PAGE_SIZE;
        Pageable page;
        if (index > availablePages) {
            page = PageRequest.of(1, PAGE_SIZE);
        } else {
            page = PageRequest.of(index, PAGE_SIZE);
        }

        return repository.findAll(page);
    }

    public List<Integer> getPageList() {
        Long count = repository.count();
        Long availablePages = count / PAGE_SIZE;
        List<Integer> pages = new ArrayList<>();

        for (int i = 0 ; i < availablePages + 1; i++) {
            pages.add(i);
        }

        return pages;
    }
}
