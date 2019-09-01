package com.smanager.dao.models;

public enum FileType {
    ASSIGNMENT("Assignment"),
    SOLUTION("Solution");

    private String name;

    FileType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
