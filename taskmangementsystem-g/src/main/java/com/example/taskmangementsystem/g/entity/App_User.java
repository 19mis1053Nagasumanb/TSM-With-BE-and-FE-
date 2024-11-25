package com.example.taskmangementsystem.g.entity;

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
}
