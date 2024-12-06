package com.example.taskmangementsystem.g.services;

import com.example.taskmangementsystem.g.models.App_User;
import com.example.taskmangementsystem.g.repositorys.UserRepository;
import com.example.taskmangementsystem.g.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;



    public String signup(App_User app_user) {
        if (userRepository.findByUsernameIgnoreCase(app_user.getUsername()) != null) {
            return "Username already exists";
        }
        String role = app_user.getRole();
        if (role == null || (!role.equals("ADMIN") && !role.equals("USER"))) {
            return "Invalid role. Only 'ADMIN' and 'USER' are allowed.";
        }
        app_user.setPassword(passwordEncoder.encode(app_user.getPassword()));
        userRepository.save(app_user);
        return "User registered successfully";
    }

//    public String login( String email, String password) {
//        App_User app_user = userRepository.findByEmail(email);
////        if (app_user != null && app_user.getPassword().equals(password)) {
////            return "Login successful .welcome" + app_user.getUsername();
////        }
//        if (app_user != null && passwordEncoder.matches(password, app_user.getPassword())) {
//            return "Login successful. Welcome " + app_user.getUsername();
//        }
//        return "Invalid username or password";
//    }


    public String login(String email, String password) {
        App_User user = userRepository.findByEmail(email);

        if (user == null) {
            return "Invalid email";
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            return "Invalid password";
        }

        // Generate JWT token here if needed
        String token = jwtUtil.generateToken(email);
         System.out.println("Login successful.Token:");
        return token;



    }


}
