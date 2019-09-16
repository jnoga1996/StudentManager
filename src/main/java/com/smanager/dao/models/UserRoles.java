package com.smanager.dao.models;

public enum UserRoles {
    ADMIN("ADMIN"),
    STUDENT("STUDENT"),
    TEACHER("TEACHER");

    private String role;

    UserRoles(String role) { this.role = role; }

    public String getRole() {
        return role;
    }
}
