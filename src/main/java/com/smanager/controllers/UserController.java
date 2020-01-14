package com.smanager.controllers;

import com.smanager.dao.models.Student;
import com.smanager.dao.models.Teacher;
import com.smanager.dao.models.User;
import com.smanager.dao.models.UserType;
import com.smanager.dao.repositories.StudentRepository;
import com.smanager.dao.repositories.TeacherRepository;
import com.smanager.dao.repositories.UserRepository;
import com.smanager.wrappers.UserWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/User")
public class UserController {

    private final static String INDEX_REDIRECT = "redirect:/";
    private final static String CREATE_REDIRECT = "redirect:/User/Create";

    private UserRepository userRepository;
    private StudentRepository studentRepository;
    private TeacherRepository teacherRepository;
    private InMemoryUserDetailsManager inMemoryUserDetailsManager;

    @Autowired
    public UserController(UserRepository userRepository, StudentRepository studentRepository,
                          TeacherRepository teacherRepository, InMemoryUserDetailsManager inMemoryUserDetailsManager) {
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
        this.inMemoryUserDetailsManager = inMemoryUserDetailsManager;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/Create")
    public String create(Model model) {
        model.addAttribute("userWrapper", new UserWrapper());
        model.addAttribute("roles", UserType.values());
        return "user_form";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/Create")
    public String createUser(UserWrapper user) {
        if (user == null || !user.getPassword().equals(user.getPasswordRetyped())) {
            return CREATE_REDIRECT;
        }

        UserType role = UserType.valueOf(user.getRole());
        String username = user.getUserName().trim();
        String firstName = user.getFirstName().trim();
        String lastName = user.getLastName().trim();

        String password = user.getPassword();
        User userToCreate = new User(username, password, role.name(), true);
        userRepository.save(userToCreate);
        Teacher teacher;
        Student student;
        if (role == UserType.TEACHER) {
            teacher = new Teacher(firstName, lastName);
            teacher.setUser(userToCreate);
            teacherRepository.save(teacher);
            userToCreate.setTeacherUser(teacher);
        } else if (role == UserType.STUDENT) {
            student = new Student(firstName, lastName, "123", 1);
            student.setUser(userToCreate);
            studentRepository.save(student);
            userToCreate.setStudentUser(student);
        }

        userRepository.save(userToCreate);

        insertIntoUserDetailsManager(role, username, password);

        return "redirect:/";
    }

    private void insertIntoUserDetailsManager(UserType role, String username, String password) {
        UserDetails userDetails = org.springframework.security.core.userdetails.User.withDefaultPasswordEncoder()
                .username(username)
                .password(password)
                .roles(role.name())
                .build();
        inMemoryUserDetailsManager.createUser(userDetails);
    }
}
