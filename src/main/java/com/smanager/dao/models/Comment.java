package com.smanager.dao.models;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "COMMENTS")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @NotBlank(message = "Content can't be blank!")
    private String content;

    private LocalDateTime creationDate;

    @ManyToOne
    @JoinColumn(name = "comment_solution_id", referencedColumnName = "solution_id")
    private Solution solution;

    @ManyToOne
    @JoinColumn(name = "student_comment_id")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "teacher_comment_id")
    private Teacher teacher;

    public Comment() {}

    public Comment(String content, LocalDateTime creationDate, Solution solution) {
        this.content = content;
        this.creationDate = creationDate;
        this.solution = solution;
    }

    public Comment(String content, LocalDateTime creationDate, Solution solution, Student student, Teacher teacher) {
        this(content, creationDate, solution);
        this.student = student;
        this.teacher = teacher;
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

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public Solution getSolution() {
        return solution;
    }

    public void setSolution(Solution solution) {
        this.solution = solution;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }
}
