package com.smanager.dao.models;

import org.hibernate.validator.constraints.Range;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Set;

@Entity
@Table(name = "SOLUTIONS")
public class Solution {
    @Column(name = "solution_id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Student student;

    @NotBlank
    private String content;

    @Range(min = 2, max = 5)
    private Double grade;

    @OneToMany(mappedBy = "assignment", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<AssignmentSolution> assignments;

    private String path;

    public Solution() {};

    public Solution(Student student, String content) {
        this.student = student;
        this.content = content;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Double getGrade() {
        return grade;
    }

    public void setGrade(Double grade) {
        this.grade = grade;
    }

    public Set<AssignmentSolution> getAssignments() {
        return assignments;
    }

    public void setAssignments(Set<AssignmentSolution> assignments) {
        this.assignments = assignments;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
