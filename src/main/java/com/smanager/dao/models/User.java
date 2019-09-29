package com.smanager.dao.models;

import javax.persistence.*;

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

    private Integer role;

    @OneToOne
    @JoinColumn(name = "student_id")
    private Student studentUser;

    @OneToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacherUser;

    public User() {}

    public User(String userName, String password, UserType userType, boolean enabled) {
        this.username = userName;
        this.password = password;
        this.role = UserType.valueOf(userType.toString()).ordinal();
    }

    public User(String userName, String password, UserType userType, boolean enabled, Student student) {
        this(userName, password, userType, enabled);
        this.studentUser = student;
    }

    public User(String userName, String password, UserType userType, boolean enabled, Teacher teacher) {
        this(userName, password, userType, enabled);
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

    public Integer getRole() {
        return role;
    }

    public void setRole(Integer role) {
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
}
