package com.smanager.wrappers;

import java.util.List;

public class GradeWrapper {
    private Integer grade;
    private List<Integer> grades;

    public GradeWrapper(Integer grade, List<Integer> grades) {
        this.grade = grade;
        this.grades = grades;
    }

    public Integer getGrade() {
        return grade;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }

    public List<Integer> getGrades() {
        return grades;
    }

    public void setGrades(List<Integer> grades) {
        this.grades = grades;
    }
}
