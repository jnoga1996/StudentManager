package com.smanager.dao.models;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "USERS")
public class User {
    @Column(name = "user_id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "enabled")
    private boolean enabled;

    private String role;

    @OneToOne
    @JoinColumn(name = "student_id")
    private Student studentUser;

    @OneToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacherUser;

    public User() {}

    public User(String userName, String password, String role, boolean enabled) {
        this.username = userName;
        this.password = password;
        this.role = role;
    }

    public User(String userName, String password, String role, boolean enabled, Student student) {
        this(userName, password, role, enabled);
        this.studentUser = student;
    }

    public User(String userName, String password, String role, boolean enabled, Teacher teacher) {
        this(userName, password, role, enabled);
        this.teacherUser = teacher;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Student getStudentUser() {
        return studentUser;
    }

    public void setStudentUser(Student studentUser) {
        this.studentUser = studentUser;
    }

    public Teacher getTeacherUser() {
        return teacherUser;
    }

    public void setTeacherUser(Teacher teacherUser) {
        this.teacherUser = teacherUser;
    }

    public boolean isStudent() {
        return (getStudentUser() != null && getTeacherUser() == null);
    }

    @Override
    public String toString() {
        return isStudent() ? getStudentUser().toString() : getTeacherUser().toString();
    }
}
