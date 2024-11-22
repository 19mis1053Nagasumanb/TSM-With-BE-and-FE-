package com.example.taskmangementsystem.g.repository;

import com.example.taskmangementsystem.g.entity.Task;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface TaskRepo extends ElasticsearchRepository<Task, String> {


}
