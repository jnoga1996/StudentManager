package com.smanager.wrappers;

import com.smanager.dao.models.Teacher;
import com.smanager.dao.models.User;

public class ReportWrapper {
    private boolean errorOccurred;
    private User user;
    private Teacher teacher;

    public boolean isErrorOccurred() {
        return errorOccurred;
    }

    public void setErrorOccurred(boolean errorOccurred) {
        this.errorOccurred = errorOccurred;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public ReportWrapper(boolean result, User user, Teacher teacher) {
        this.errorOccurred = result;
        this.user = user;
        this.teacher = teacher;
    }
}
