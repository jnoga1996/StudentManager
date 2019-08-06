package com.smanager.dao.models;

import javax.persistence.*;

@Entity
@Table(name = "GROUPSS")
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_id")
    private Long id;

    @OneToOne
    private Course course;

    @OneToOne
    private Teacher teacher;

    public Group() {}

    public Group(Course course, Teacher teacher) {
        this.course = course;
        this.teacher = teacher;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }
}
