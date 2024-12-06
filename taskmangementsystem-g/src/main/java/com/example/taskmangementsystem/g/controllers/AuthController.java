package com.example.taskmangementsystem.g.controllers;

import com.example.taskmangementsystem.g.models.App_User;
import com.example.taskmangementsystem.g.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

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
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        // Invalidate the JWT token from the client-side
        // Invalidate session if using HttpSession-based authentication
        request.getSession().invalidate(); // Invalidate session if necessary
        response.setHeader("Authorization", ""); // Remove any authorization headers if applicable
        return ResponseEntity.ok().build();
    }

    @GetMapping("/user-info")
    public ResponseEntity<Map<String, String>> getUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Map<String, String> response = new HashMap<>();

        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails) {
                String username = ((UserDetails) principal).getUsername();
                String role = ((UserDetails) principal).getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .findFirst()
                        .orElse("ROLE_USER");

                response.put("username", username);
                response.put("role", role);
                return ResponseEntity.ok(response);
            }
        }

        response.put("error", "Unauthorized");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }


}
