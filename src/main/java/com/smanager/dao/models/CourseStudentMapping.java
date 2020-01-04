package com.smanager.dao.models;

public class CourseStudentMapping extends CourseUserTypeMapping {

    private Student student;

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }
}
