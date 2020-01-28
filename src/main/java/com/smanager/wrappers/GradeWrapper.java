package com.smanager.wrappers;

import java.util.List;

public class GradeWrapper {
    private Double grade;
    private List<Double> grades;

    public GradeWrapper(Double grade, List<Double> grades) {
        this.grade = grade;
        this.grades = grades;
    }

    public Double getGrade() {
        return grade;
    }

    public void setGrade(Double grade) {
        this.grade = grade;
    }

    public List<Double> getGrades() {
        return grades;
    }

    public void setGrades(List<Double> grades) {
        this.grades = grades;
    }
}
