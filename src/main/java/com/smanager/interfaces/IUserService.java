package com.smanager.interfaces;

import com.smanager.dao.models.User;

public interface IUserService {

    User getLoggedUser();

    Long getStudentOrTeacherId(User user);
}
