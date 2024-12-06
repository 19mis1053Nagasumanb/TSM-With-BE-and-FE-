package com.example.taskmangementsystem.g.repositorys;

import com.example.taskmangementsystem.g.models.DbTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.UUID;

@EnableJpaRepositories
public interface TaskRepository extends JpaRepository<DbTask, UUID> {

}
