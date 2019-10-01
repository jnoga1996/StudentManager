package com.smanager.dao.models;

import org.hibernate.validator.constraints.Range;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "SOLUTIONS")
public class Solution implements ISaveable{
    @Column(name = "solution_id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Student student;

    @NotBlank
    private String content;

    private Double grade;

    @ManyToOne
    private Assignment assignment;

    private String path;

    private boolean finished;

    private LocalDateTime creationDate;

    @OneToMany(mappedBy = "solution")
    private Set<Comment> comments;

    public Solution() {
        this.creationDate = LocalDateTime.now();
    };

    public Solution(Student student, String content) {
        this();
        this.student = student;
        this.content = content;
    }

    public Solution(Student student, String content, Grades grade) {
        this(student, content);
        this.grade = grade.getGrade();
    }

    public Solution(Student student, String content, Grades grade, LocalDateTime creationDate) {
        this(student, content, grade);
        this.creationDate = creationDate;
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

    public Assignment getAssignment() {
        return assignment;
    }

    public void setAssignment(Assignment assignment) {
        this.assignment = assignment;
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

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Id:");
        sb.append(getId());
        sb.append(", content:");
        sb.append(getContent());

        return sb.toString();
    }
}
