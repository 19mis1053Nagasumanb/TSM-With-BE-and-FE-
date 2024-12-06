package com.example.taskmangementsystem.g.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "App_User")
public class App_User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String email;


    @Column(nullable = false)
    private String password;

    @Column(nullable = true)
    private String role; // ROLE_USER or ROLE_ADMIN


}
