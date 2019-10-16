package com.smanager.dao.models;

public enum Grades {
    A(5.0),
    B(4.5),
    C(4.0),
    D(3.5),
    E(3.0),
    F(2.0),
    NO_GRADE(null);

    private Double grade;

    Grades(Double grade) {
        this.grade = grade;
    }

    public Double getGrade() {
        return grade;
    }
}
