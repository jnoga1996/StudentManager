package com.smanager.dao.models;

import org.hibernate.validator.constraints.Range;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Set;

@Entity
@Table(name = "STUDENTS")
@EntityListeners(AuditingEntityListener.class)
public class Student implements IUser {
    @Column(name = "student_id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotBlank
    private String faculty;

    @Range(min = 0, max = 5)
    private int currentYear;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Solution> solutions;

    @ManyToMany(mappedBy = "students")
    private Set<Course> courses;

    @OneToMany
    @JoinColumn(name = "student_comment_id")
    private Set<Comment> comments;

    @OneToOne(mappedBy = "studentUser")
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User userId) {
        this.user = user;
    }

    public Student() {};

    public Student(String firstName, String lastName, String faculty, int currentYear) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.faculty  = faculty;
        this.currentYear = currentYear;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public int getCurrentYear() {
        return currentYear;
    }

    public void setCurrentYear(int currentYear) {
        this.currentYear = currentYear;
    }

    public Set<Solution> getSolutions() {
        return solutions;
    }

    public void setSolutions(Set<Solution> solutions) {
        this.solutions = solutions;
    }

    public Set<Course> getCourses() {
        return courses;
    }

    public void setCourses(Set<Course> courses) {
        this.courses = courses;
    }

    public Set<Comment> getComments() {
        return comments;
    }

    public void setComments(Set<Comment> comments) {
        this.comments = comments;
    }

    @Override
    public String toString() {
        return "Name: " + getFirstName() + " " + getLastName()
                + ", faculty: " + getFaculty() + ", year: " + getCurrentYear();
    }

    public String getUsername() {
        return String.format("%s.%s", getFirstName(), getLastName());
    }
}
