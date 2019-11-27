package com.smanager.dao.models;

import com.smanager.controllers.SolutionController;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
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
        setFinished(true);
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

    public Set<Comment> getComments() {
        return comments;
    }

    public void setComments(Set<Comment> comments) {
        this.comments = comments;
    }

    public String getDetailsUrl() {
        return SolutionController.DETAILS_URL + "?id=" + getId();
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

    public String getPath(List<String> paths) {
        if (paths != null) {
            for (String path : paths) {
                if (path.contains(getPath())) {
                    return path;
                }
            }
        }

        return null;
    }

    public String getFormattedDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm");
        String formattedDate = getCreationDate().format(formatter);
        return formattedDate;
    }

}
