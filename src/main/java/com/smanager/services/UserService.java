package com.smanager.services;

import com.smanager.dao.models.User;
import com.smanager.dao.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class UserService {

    private Authentication authentication;
    private UserRepository userRepository;

    @Autowired
    public UserService(Authentication authentication, UserRepository userRepository) {
        this.authentication = authentication;
        this.userRepository = userRepository;
    }

    public User getLoggedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        User user = userRepository.getUserByUsername(currentUsername);

        return user;
    }

    public Long getStudentOrTeacherId(User user) {
        if (user != null) {
            if (user.getStudentUser() != null) {
                return user.getStudentUser().getId();
            } else if (user.getTeacherUser() != null) {
                return user.getTeacherUser().getId();
            }
        }

        return Long.valueOf(-1);
    }
}
