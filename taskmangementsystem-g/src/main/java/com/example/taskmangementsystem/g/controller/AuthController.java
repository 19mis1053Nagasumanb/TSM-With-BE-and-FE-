package com.example.taskmangementsystem.g.controller;

import com.example.taskmangementsystem.g.entity.App_User;
import com.example.taskmangementsystem.g.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/signup")
    public String signup(@RequestBody App_User app_user) {
        return userService.signup(app_user);
    }

    @PostMapping("/login")
    public String login(@RequestBody App_User app_user) {
        return userService.login(app_user.getEmail(), app_user.getPassword());
    }
}
