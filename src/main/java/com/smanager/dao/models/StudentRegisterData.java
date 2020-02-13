package com.smanager.dao.models;

public class StudentRegisterData {

    private Long studentId;
    private Boolean registered;

    public StudentRegisterData() {}

    public StudentRegisterData(Long studentId, Boolean registered) {
        this.studentId = studentId;
        this.registered = registered;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Boolean getRegistered() {
        return registered;
    }

    public void setRegistered(Boolean registered) {
        this.registered = registered;
    }
}
