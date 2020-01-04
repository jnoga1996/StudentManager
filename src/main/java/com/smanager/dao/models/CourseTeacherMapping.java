package com.smanager.dao.models;

public class CourseTeacherMapping extends CourseUserTypeMapping {

    private Teacher teacher;

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }
}
