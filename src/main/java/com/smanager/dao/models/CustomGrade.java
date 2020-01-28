package com.smanager.dao.models;

public class CustomGrade {

    Long courseId;
    Double grade;

    public CustomGrade(Long courseId, Double grade) {
        this.courseId = courseId;
        this.grade = grade;
    }

    public Long getCourseId() {
        return courseId;
    }

    public Double getGrade() {
        return grade;
    }
}
