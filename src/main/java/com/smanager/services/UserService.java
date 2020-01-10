package com.smanager.services;

import com.smanager.dao.models.User;
import com.smanager.dao.repositories.UserRepository;
import com.smanager.interfaces.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements IUserService {

    private Authentication authentication;
    private UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getLoggedUser() {
        authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String currentUsername = authentication.getName();
            return userRepository.getUserByUsername(currentUsername);
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
