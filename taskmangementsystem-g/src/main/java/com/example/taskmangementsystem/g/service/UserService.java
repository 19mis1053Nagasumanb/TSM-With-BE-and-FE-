package com.example.taskmangementsystem.g.service;

import com.example.taskmangementsystem.g.entity.App_User;
import com.example.taskmangementsystem.g.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public String signup(App_User app_user) {
        if (userRepository.findByUsernameIgnoreCase(app_user.getUsername()) != null) {
            return "Username already exists";
        }
        userRepository.save(app_user);
        return "User registered successfully";
    }

    public String login( String email, String password) {
        App_User app_user = userRepository.findByEmail(email);
        if (app_user != null && app_user.getPassword().equals(password)) {
            return "Login successful .welcome" + app_user.getUsername();
        }
        return "Invalid username or password";
    }
}
