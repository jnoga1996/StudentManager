package com.smanager.wrappers;

import com.smanager.dao.models.Course;
import com.smanager.dao.models.StudentRegisterData;

import java.util.List;

public class StudentRegisterWrapper {

    private Course course;
    private List<Long> studentIdList;
    private List<Boolean> registeredList;
    private List<Long> registeredStudentIdList;
    private List<Boolean> unregisteredList;

    public StudentRegisterWrapper() {}

    public StudentRegisterWrapper(Course course, List<Long> studentIdList, List<Boolean> registeredList,
                                  List<Long> registeredStudentIdList, List<Boolean> unregisteredList) {
        this.course = course;
        this.studentIdList = studentIdList;
        this.registeredList = registeredList;
        this.registeredStudentIdList = registeredStudentIdList;
        this.unregisteredList = unregisteredList;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public List<Long> getStudentIdList() {
        return studentIdList;
    }

    public void setStudentIdList(List<Long> studentIdList) {
        this.studentIdList = studentIdList;
    }

    public List<Boolean> getRegisteredList() {
        return registeredList;
    }

    public void setRegisteredList(List<Boolean> registeredList) {
        this.registeredList = registeredList;
    }

    public List<Long> getRegisteredStudentIdList() {
        return registeredStudentIdList;
    }

    public void setRegisteredStudentIdList(List<Long> registeredStudentIdList) {
        this.registeredStudentIdList = registeredStudentIdList;
    }

    public List<Boolean> getUnregisteredList() {
        return unregisteredList;
    }

    public void setUnregisteredList(List<Boolean> unregisteredList) {
        this.unregisteredList = unregisteredList;
    }
}
