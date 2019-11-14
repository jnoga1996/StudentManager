package com.smanager.services;

import com.smanager.dao.models.User;
import com.smanager.dao.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private Authentication authentication;
    @Autowired
    private UserRepository userRepository;

    public User getLoggedUser() {
        authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String currentUsername = authentication.getName();
            User user = userRepository.getUserByUsername(currentUsername);

            return user;
        }

        return null;
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
